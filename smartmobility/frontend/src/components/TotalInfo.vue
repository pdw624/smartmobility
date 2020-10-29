<template>
    <div>
        <Layout style="width: 100%; height: 800px">
            <LayoutPanel region="north" :border="false" style="height: 45px; text-align: right;">
                <Buttons :buttonEvents="buttonEvents"/>
            </LayoutPanel>

            <LayoutPanel region="west" title="수행기록" style="width: 400px;">
                <DataGrid
                    ref="historyList"
                    :data="data"
                    :columnResizing="true"
                    :border="false"
                    :loading="loading"
                    selectionMode="single"
                    @selectionChange="historyClick($event)">
                    
                    <GridColumn cellCss="datagrid-td-rownumber" width="30" align="center">
                        <template slot="header">
                            No.
                        </template>
                        <template slot="body" slot-scope="scope">
                            {{ scope.rowIndex + 1 }}
                        </template>
                    </GridColumn>
                    <GridColumn field="workDatetime" title="수행일시" width="160" align="center" sortable></GridColumn>
                    <GridColumn field="workName" title="수행명" align="left" sortable></GridColumn>
                    <GridColumn field="rstCount" title="수행건수" align="center" width="70" sortable></GridColumn>
                </DataGrid>
            </LayoutPanel>

            <LayoutPanel region="center" :border="false" style="width: 100%; height: 100%;">
                <Layout ref="container">
                    <LayoutPanel region="north" title="수행결과" style="width: 100%; height: 380px;">
                        graph1
                    </LayoutPanel>
                    
                    <LayoutPanel region="west" style="width: 100%; height: 380px;">
                        graph2
                    </LayoutPanel>

                </Layout>
            </LayoutPanel>
        </Layout>

        <el-dialog title="결과 내용" :visible.sync="resultContent" width="800px" custom-class="dialog-h">
            <vue-json-pretty
                :data="jsonData"
                :showLength="true"
                :showDoubleQuotes="false"
                :showSelectController="true"
                :highlightMouseoverNode="true">
            </vue-json-pretty>
        </el-dialog>
    </div>
</template>

<script>
import Buttons from './Buttons'
import VueJsonPretty from 'vue-json-pretty'
import XLSX from 'xlsx'

export default {
    components: {
        Buttons,
        VueJsonPretty,
    },
    data() {
        return {
            jsonData: {},
            htmlData: '',
            resultContent: false,
            buttonEvents: {
                refresh: {
                    show: true,
                    event: () => {
                        this.loadGrid()
                    }
                },
                download: {
                    show: true,
                    event: this.downloadExcel
                }
            },

            // 라인차트 데이터
            //series: [],
            series: [{
                //data: [[1, 34], [3, 54], [5, 23] , [15, 43]]
                data:[]
            }], 
            testSeries: [{
                //data: [[1, 34], [3, 54], [5, 23] , [15, 43]]
                data:[]
            }], 
            // 파이차트 데이터
            pieSeries: [0, 0],

            chartTitle: 'TPS 차트',
            // 라인 차트 옵션
            chartOptions: {
                /*
                title: {
                    text: 'TPS 차트',
                    align: 'center',
                },
                //*/

                series:[],

                chart: {
                    id: 'chartLine',
                    toolbar: {
                        show: false,
                    },
                    zoom: {
                        type: 'x',
                        enabled: true,
                        autoScaleYaxis: true
                    },
                },
                stroke: {
                    width: 3
                },
                markers: {
                    size: 0,
                    strokeWidth: 2,
                },
                legend: {
                    position: 'top'
                },
                xaxis: {
                    title: {
                        text: 'Second'
                    },
                    labels: {
                        //show: false,
                        show: true,
                        hideOverlappingLabels: true,
                    },
                    axisBorder: {
                        show: false
                    },
                    axisTicks: {
                        show: false
                    },
                    tooltip: {
                        enabled: false
                    },
                    
                    

                    //min: this.min
                    //,max: this.max,

                    //(max - min)이 1초단위 표현            input : 1 >> max -min

                    //소수점 둘째자리 아래는 표현이 안됨 ex) 0.01 X
                    //(max - min) * 10이 0.1초단위 표현     input : 0.1 >> (max - min) * 100
                    //(max - min) * 0.1이 10초단위 표현     input : 10 >> (max - min) * 0.1
                    //(max - min) * 0.01이 100초단위 표현     input : 100 >> (max - min) * 0.01
                    
                    //즉, input 입력 시 (1/input)을 곱해주면 됨
                    //type: "time",
                    //type: "numeric",
                    //min: this.xmin,
                    //max: this.xmax,
                    //tickAmount: this.xtick
                    
                    //tickInterval:10* 1000,

                    
                    tickAmount: 4,

                    
                },
                
                yaxis: {
                    title: {
                        //text: '응답시간(milliseconds)'
                        text: 'TPS Count'
                    },
                    tickAmount: 4,
                    /*
                    labels: {
                        formatter: function(val, opts) {
                            return val + 'ms'
                        }
                    },
                    //*/
                    min: 0,
                    // max: 15000,
                }
            },
            // 파이 차트 옵션
            pieChartOptions: {
                title: {
                    text: 'SUCCESS/FAIL 차트',
                    align: 'center'
                },
                colors: ['#00E397', '#FF7979',],
                labels: ['SUCCESS', 'FAIL'],
                legend: {
                    position: 'top'
                },
            },
            
            focusIndex: 0,
            data: [],
            loading: false,
            logData: [],
            logLoading: false,
            logTotalCount: 0,
            pageSize: 50,
            pagePosition: 'bottom',

            //x축 간격 위한 min, max
            xmax:0,
            xmin:0,
            xtick:0
        }
    },

    created() {
        this.loadGrid()
    },

    methods: {
        loadGrid() {
            this.loading = true
            this.axios.get('/api/v1/statistics').then(response => {
                this.loading = false

                response.data.historyList.forEach((row, index) => {
                    row['index'] = index
                })

                this.data = response.data.historyList
                this.$refs.historyList.selectRow(this.data[this.focusIndex])
            }).catch(ex => {
                console.log(ex)
                this.loading = false
            })
        },

        historyClick(row) {
            if(row) {
                this.focusIndex = row.index
                this.logLoading = true

                var loadingInstance = this.$loading({
                    lock: true,
                    text: 'Data Loading...',
                    spinner: 'el-icon-loading',
                    target: this.$refs.container.$el,
                    fullscreen: false,
                })

                this.axios.get('/api/v1/statistics/result', {
                    params: {
                        workSeq: row.workSeq
                    }
                }).then(response => {
                    loadingInstance.close()
                    var resultList = response.data.resultList
                    var graphList = response.data.graphList

                    this.logTotalCount = resultList.length
                    this.logData = resultList
                    this.logLoading = false


                    /*************************************************** 라인 차트 데이터 가공 *****************************************************/
                    var categories = []
                    var arrCountStartRequest = []
                    var arrCountEndRequest = []
                    var sumEndRequest = 0;
                    var series = []

                    var testSeries = []

                    graphList.forEach((result, index, list) => {
                        categories.push(result.resultTime)
                        
                        
                        //park
                        testSeries.push(result.resultTime)
                        
                        console.log("총길이 : "+list.length)
                        if(index == 0){
                            this.xmin = result.resultTime
                        }
                        else if(index == list.length-1){
                            this.xmax = result.resultTime
                        }
                        console.log('hello ['+index+"] "+ result.resultTime)//////////////
                        //
                        
                        arrCountStartRequest.push(result.countStartRequest)
                        
                        arrCountEndRequest.push(result.countEndRequest)
                        
                        sumEndRequest += result.countEndRequest
                        
                    })
                    this.xtick = this.xmax<this.xmin ? Number(60)+Number(this.xmax)-this.xmin : this.xmax-this.xmin
                    //this.tick = this.max - this.min
                    
                    console.log('끝 : '+this.xmin, this.xmax, this.xtick)//
                    console.log("카테고리 : "+categories)
                    console.log("시리즈 : "+testSeries)
                    
                    testSeries.push({
                        name: '초',
                        data: testSeries
                    })
                    
                    
                    this.$refs.lineChart.updateOptions({
                        xaxis: {
                            //categories: categories,
                            //categories: testSeries,
                            labels: categories
                            //testSeries: series,
                        },
                    })
                    

                    series.push({
                        name: '요청수',
                        data: arrCountStartRequest
                    })

                    series.push({
                        name: '응답수',
                        data: arrCountEndRequest
                    })

                    this.$refs.lineChart.updateSeries(series)
                    //this.$refs.lineChart.updateSeries(testSeries)
                    this.chartTitle = '평균 TPS: ' + Math.round((sumEndRequest / graphList.length))
                    //*/
                    /*****************************************************************************************************************************/



                    /*************************************************** 라인 차트 데이터 가공 *****************************************************/
                    /*
                    var categories = []
                    var series = []
                    var tempArray = []
                    var max = resultList[0].resTime

                    resultList.forEach((result, index, list) => {
                        var indexOf;

                        if(row.workType == 'AT')
                            indexOf = 2
                        else if(row.workType == 'SR')
                            indexOf = 4

                        var rstName = result.rstName.substring(0, result.rstName.indexOf('-') + indexOf) + ' 루프'

                        if(max < result.resTime)
                            max = result.resTime
                        
                        tempArray.push(result.resTime)
                        if(index + 1 == list.length) {
                            series.push({
                                name: rstName,
                                data: [...tempArray]
                            })
                            return
                        }

                        var rstNameNums = result.rstName.substring(result.rstName.indexOf('-') + 1, result.rstName.length).split('-');
                        var nextRstNameNums = list[index + 1].rstName.substring(list[index + 1].rstName.indexOf('-') + 1, list[index + 1].rstName.length).split('-')

                        if(row.workType == 'AT') {
                            if(rstNameNums[0] != nextRstNameNums[0]) {
                                series.push({
                                    name: rstName,
                                    data: [...tempArray]
                                })
                                tempArray = []
                            }
                        }
                        else if(row.workType == 'SR') {
                            if(rstNameNums[0] != nextRstNameNums[0] || rstNameNums[1] != nextRstNameNums[1]) {
                                series.push({
                                    name: rstName,
                                    data: [...tempArray]
                                })
                                tempArray = []
                            }
                        }

                        categories.push(result.rstName)
                    })

                    this.$refs.lineChart.updateOptions({
                        xaxis: {
                            categories: categories
                        },
                    })

                    this.$refs.lineChart.updateSeries(series)
                    //*/
                    /*****************************************************************************************************************************/

                    /*************************************************** 파이 차트 데이터 가공 *****************************************************/
                    /*
                    var pieData = []
                    pieData.push(row.successCount)
                    pieData.push(row.failCount)

                    this.$refs.pieChart.updateSeries(pieData)
                    //*/
                    /*****************************************************************************************************************************/
                }).catch(ex => {
                    this.logLoading = false
                    loadingInstance.close()
                    console.log(ex)
                })
            }
        },

        resTimeCellCss(row, value) {
            if(row.rstType == 'FAIL') {
                return { background: '#FF7979' }
            }
        },

        dbClickResult(row) {
            this.resultContent = true
            try {
                this.jsonData = JSON.parse(row.rstContent)
            } catch(e) {
                this.jsonData = row.rstContent
            }
            
        },

        downloadExcel() {
            if(this.logLoading) {
                this.notification('downloadExcel')
            } else {
                var excelData = []

                this.logData.forEach((value, index) => {
                    excelData.push({
                        'No': index + 1,
                        '수행명': value.rstName,
                        '수행일시': "'" + value.startDatetime,
                        '수행종료일시': "'" + value.endDatetime,
                        '응답시간(ms)': value.resTime,
                        '내용': value.rstContent
                    })
                })
                const wb = XLSX.utils.book_new()
                const ws = XLSX.utils.json_to_sheet(excelData)
                XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
                XLSX.writeFile(wb, 'log.csv')
                //*/
            }
        }
    }
}
</script>

<style scoped>
#chart {
    margin-top: 10px;
}

/*
#lineChart {
    display: inline-block;
}
//*/

#pieChart {
    display: inline-block;
}

.vjs-tree {
    height: 500px;
    overflow: auto;
}

.dialog-h {
    height: 600px;
}

</style>