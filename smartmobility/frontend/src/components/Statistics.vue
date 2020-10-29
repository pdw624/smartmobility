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
                    <LayoutPanel region="north" title="수행결과" style="width: 100%; height: 225px;">
                        <!--div style="width: 100%; text-align: center; margin-top: 10px;">{{ chartTitle }}</div>
                        <div id="chart">
                            <apexchart id="lineChart" ref="lineChart" type="line" height="280" :options="chartOptions" :series="series" />
                        </div-->
                          <template>
                            <el-table
                            :data="tableData"
                            
                            style="width: 100%"><!--100%--><!--3000px-->
                                <el-table-column 
                                    prop="header1"
                                    label=""
                                    width="180">
                                    
                                </el-table-column>
                                <el-table-column
                                    prop="data1"
                                    label=""
                                    width="200">
                                </el-table-column>
                                <el-table-column
                                    prop="blank"
                                    label="">
                                </el-table-column>
                                <el-table-column
                                    prop="header2"
                                    label="">
                                </el-table-column>
                                <el-table-column
                                    prop="data2"
                                    label="[종합집계]"
                                    width="200"
                                >
                                </el-table-column>
                                <el-table-column
                                    prop="header3"
                                    label="">
                                </el-table-column>
                                <el-table-column
                                    prop="data3"
                                    label="">
                                </el-table-column>
                                <el-table-column
                                    prop="blank"
                                    label="">
                                </el-table-column>
                                <el-table-column
                                    prop="blank"
                                    label="">
                                </el-table-column>
                            </el-table>
                        </template>
                        
                    </LayoutPanel>
                    
                    <LayoutPanel region="center" style="width: 100%; height: 550px;">
                        <div style="width: 100%; text-align: center; margin-top: 10px;">
                            {{ chartTitle }}
                            <div style="float:right">
                                <label>단위</label>&nbsp &nbsp
                                <el-select v-model="value" placeholder="Select" style="width:40%; height:20px" @change="xaxisChange()">
                                    <el-option 
                                    
                                    v-for="item in options"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                    
                                    >
                                    </el-option>
                                </el-select>
                            </div>
                        </div>
                        
                        <div id="chart">
                            <apexchart id="lineChart" ref="lineChart" type="line" height="280" :options="chartOptions" :series="series" />
                            <!-- <apexchart id="pieChart"  ref="pieChart" type="pie" width="300" height="300" :options="pieChartOptions" :series="pieSeries" /> -->
                        </div>
                        <DataGrid
                            ref="resultList"
                            :data="logData"
                            :total="logTotalCount"
                            :pagination="true"
                            :pageSize="pageSize"
                            :pagePosition="pagePosition"
                            :columnResizing="true"
                            :border="false"
                            @rowDblClick="dbClickResult"
                            style="width: 100%; height: 200px;">

                            <GridColumn cellCss="datagrid-td-rownumber" width="60" align="center">
                                <template slot="header">
                                    No.
                                </template>
                                <template slot="body" slot-scope="scope">
                                    {{ scope.rowIndex + 1 }}
                                </template>
                            </GridColumn>
                            <GridColumn field="rstName" title="수행명" width="150" align="center" sortable></GridColumn>
                            <GridColumn field="startDatetime" title="수행일시" width="200" align="center" sortable></GridColumn>
                            <GridColumn field="endDatetime" title="수행종료일시" width="200" align="center" sortable></GridColumn>
                            <GridColumn field="resTime" title="응답시간(ms)" :cellCss="resTimeCellCss" width="100" align="center" sortable></GridColumn>
                            <GridColumn field="rstContent" title="내용" halign="center"></GridColumn>
                        </DataGrid>
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
var arrLabel = []

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
            options: [{
                value: 1,
                label: '1초'
                }, {
                value: 0.1,
                label: '10초'
                }, {
                value: 0.016,
                label: '1분'
                },{
                value: 0.00027,
                label: '1시간'
                },],
            value: 1,

            tableData: [{
                header1: '수행시작시간',
                data1: '',
                blank: '',
                header2 : '수행종료시간',
                data2 : ''
            }, {
                header1: '최대응답시간(ms)',
                data1: '',
                blank: '',
                header2: '최소응답시간(ms)',
                data2: ''
            }, {
                header1: '전체수행횟수',
                data1: '',
                blank: '',
                header2: '성공횟수',
                data2: '',
                header3: '실패횟수',
                data3: ''
            }],



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
            series: [],
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
                        formatter: function(value) {
                            var temp = Math.floor(parseInt(value))-1;
                            //console.log("formatter에서의 arrLabel : ", arrLabel[value])
                            //console.log("zz",value, temp)
                            return arrLabel[temp];//ct==category 배열 꼴로 라벨링
                            //return arrLabel[0]
                        }
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
                    type: "numeric",
                    // min: 0,
                    // max: arrLabel.length-1,
                    // tickAmount: 10,
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
            pageSize: 10,
            pagePosition: 'bottom',

            statisticArr: []
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

                //park
                console.log("timeout 찾기..",response.data.historyList)//
                this.statisticArr = response.data.historyList

                this.data = response.data.historyList
                this.$refs.historyList.selectRow(this.data[this.focusIndex])
            }).catch(ex => {
                console.log(ex)
                this.loading = false
            })
        },
        
        xaxisChange(){
            var multipleVal = this.value
            
            console.log("zz",multipleVal)
            var tickTemp = (arrLabel.length-1)*parseFloat(multipleVal)//곱해줄 값을 input 값으로 받자! select 해서 받도록 단위 1초(1), 10초(0.1), 60초(0.6), 3600초(0.00027) 
            console.log("max-min",(arrLabel.length-1))
            console.log("parseFloat(multipleVal)",parseFloat(multipleVal))
            console.log("tickTemp",tickTemp)
            console.log(tickTemp.toFixed(6))
            this.$refs.lineChart.updateOptions({
                xaxis: {
                    //categories: categories
                    min: 1,
                    max: arrLabel.length,
                    tickAmount: tickTemp
                },
            })
 
        },

        

        historyClick(row) {
            this.value=1
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

                    //park excel 메소드 참고

                    var start
                    var end
                    var maxResTime =0;
                    var minResTime;
                    // var failCount = 0;
                    // var successCount =0;

                    this.logData.forEach((value, index) => {
                        
                        //시작시간
                        if(index == 0){
                            start = value.startDatetime
                            minResTime=value.resTime
                        }//종료시간
                        else if(index == this.logData.length-1){
                            end = value.endDatetime
                        }
                        //최대응답시간
                        if(maxResTime < value.resTime){
                            maxResTime = value.resTime
                        }
                        //최소응답시간
                        if(minResTime > value.resTime){
                            minResTime = value.resTime
                        }

                        // if(value.resTime>=3000){
                        //     failCount++
                        // }else{
                        //     successCount++
                        // }
            
     
                        
                    })
                    console.log("timout 옮겨갔는지 확인",this.statisticArr)//확인 -> 모든 실행목록임 매칭시켜야함

                    console.log('시작 시간:'+  start)
                    console.log('종료 시간:'+  end)
                    console.log('최대 응답 시간:'+  maxResTime)
                    console.log('최소 응답 시간:'+  minResTime)
                    console.log('전체 수행 횟수:'+  this.logData.length)
                    console.log('성공 횟수:'+  this.statisticArr[this.focusIndex].successCount)
                    console.log('실패 횟수:'+  this.statisticArr[this.focusIndex].failCount)
                    this.tableData[0].data1 = start
                    this.tableData[0].data2 = end
                    this.tableData[1].data1 = maxResTime
                    this.tableData[1].data2 = minResTime
                    this.tableData[2].data1 = this.logData.length
                    this.tableData[2].data2 = this.statisticArr[this.focusIndex].successCount
                    this.tableData[2].data3 = this.statisticArr[this.focusIndex].failCount
                    /*************************************************** 라인 차트 데이터 가공 *****************************************************/
                    var categories = []
                    var arrCountStartRequest = []
                    var arrCountEndRequest = []
                    var sumEndRequest = 0;
                    var series = []

                    graphList.forEach((result, index, list) => {
                        categories.push(result.resultTime)
                        arrCountStartRequest.push(result.countStartRequest)
                        arrCountEndRequest.push(result.countEndRequest)
                        sumEndRequest += result.countEndRequest
                    })

                    arrLabel = categories
                    console.log("내 라벨", arrLabel)
                    // console.log("내 라벨 길이", arrLabel.length)
                    // console.log("인덱스 마지막 : ",(arrLabel.length-1)*0.1)

                    //(max - min) * 10이 0.1초단위 표현     input : 0.1 >> (max - min) * 100
                    //(max - min) * 0.1이 10초단위 표현     input : 10 >> (max - min) * 0.1
                    //(max - min) * 0.01이 100초단위 표현     input : 100 >> (max - min) * 0.01

                    //this.xaxisChange()
                    // var multipleVal = this.value
                    // console.log("zz",multipleVal)
                    // var tickTemp = (arrLabel.length-1)*1//곱해줄 값을 input 값으로 받자! select 해서 받도록 단위 1초(1), 10초(0.1), 60초(0.6), 3600초(0.00027) 
                    
                    this.$refs.lineChart.updateOptions({
                        xaxis: {
                            //categories: categories
                            min: 0,
                            max: arrLabel.length-1,
                            tickAmount: arrLabel.length-1,
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

                    console.log(series)
                    console.log(sumEndRequest)
                    console.log(graphList.length)
                    this.$refs.lineChart.updateSeries(series)
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