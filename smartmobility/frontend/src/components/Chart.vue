<template>
    <div>
        <Layout style="width: 100%">
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

            <!--apexchart id="pieChart"  ref="pieChart" type="pie" width="300" height="300" :options="pieChartOptions" :series="pieSeries" /-->    
            <Layout ref="container">
                <LayoutPanel region="east" title="응답횟수" style="width:500px;">
                    
                        <LayoutPanel region="center" :border="false" style="width:500px; height:100%;" >
                            <template>
                                <div id="colChart">
                                    <apexchart id="colChart" ref="colChart" type="bar" height="190px" :options="colChartOptions" :series="colSeries"></apexchart>
                                </div>
                                
                            </template>
                        </LayoutPanel>

                        <LayoutPanel region="south"  style="width:500px; height:60%;" >                                  
                            <div id="radialChart">
                                <apexchart id="radialChart" ref="radialChart" type="radialBar" height="420px" :options="radialChartOptions" :series="radialSeries"></apexchart>
                            </div>
                            
                        </LayoutPanel>

                    
                </LayoutPanel>
                
                <LayoutPanel region="center" :border="false" style="width: 100%; height: 100%;">
                    
                    <!--Layout ref="container"-->

                        <LayoutPanel region="center" title="수행결과" style="width: 100%; height: 33%;">
                            <div align="center " style="width:100%; margin-top: 10px;">
                                {{ areaChartTitle }}
                            </div>
                            <template>
                                    <div id="areaChart">
                                        <apexchart id="areaChart" ref="areaChart" type="line" height="170px" :options="areaChartOptions" :series="areaSeries"></apexchart>
                                    </div> 
                            </template>
                            
                        </LayoutPanel>
                        
                        <LayoutPanel region="center"  style="width: 100%; height: 33%;">
                            <!-- <template>
                                <div id="chart">
                                    <apexchart id="scatterChart" ref="scatterChart" type="scatter" width="1000px" height="230px" :options="scatterChartOptions" :series="scatterSeries"></apexchart>
                                </div>
                            </template> -->
                
                            <!-- <div id="barChart" style="width: 100%; height:100%">
                                <apexchart id="barChart" ref="barChart" type="bar"  height="230px" :options="barChartOptions" :series="barSeries"></apexchart>
                            </div> -->
                            <div style="width: 100%; text-align: center; margin-top: 10px;">
                                <div align="center " style="width:100%;">
                                    {{ heatChartTitle }}
                                </div>
                                <div align="right">
                                    <label>단위</label>&nbsp &nbsp
                                    <el-select v-model="value2" placeholder="Select"  style="width:10%; margin-right: 10px;" @change="heatXaxisChange()">
                                        <el-option 
                                        
                                        v-for="item2 in options2"
                                        :key="item2.value2"
                                        :label="item2.label2"
                                        :value="item2.value2"
                                        
                                        >
                                        </el-option>
                                    </el-select>
                                </div>
                            </div>     

                            <div id="heatChart" style="width: 100%; height:30%;">
                                <apexchart id="heatChart" ref="heatChart" type="heatmap" width="100%" height="180px" :options="heatChartOptions" :series="heatSeries"></apexchart>
                            </div>
                            
                                
                        </LayoutPanel>
                        
                        <LayoutPanel region="center"  style="width: 100%; height: 34%;" >
                            <div style="width: 100%; text-align: center; margin-top: 10px;">
                                <div align="center " style="width:100%;">
                                    {{ chartTitle }}
                                </div>
                                <div align="right">
                                    <label>단위</label>&nbsp &nbsp
                                    <el-select v-model="value" placeholder="Select"  style="width:10%; margin-right: 10px;" @change="xaxisChange()">
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
                                <apexchart id="lineChart" ref="lineChart" type="line" height="150px" :options="chartOptions" :series="series"/>
                            </div>
                        </LayoutPanel>
                    <!--/Layout-->
                </LayoutPanel>
            </Layout>
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
var areaArrLabel = []
var scatterArrLabel = []
var heatArrLabel = []

var heatYLabel = []

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

            options2: [{
                value2: 1,
                label2: '1초'
                }, {
                value2: 10,
                label2: '10초'
                }, {
                value2: 60,
                label2: '1분'
                },{
                value2: 3600,
                label2: '1시간'
                },],
            value2: 1,


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
            // 컬럼차트 데이터
            colSeries :[],
            // 에이리어차트 데이터
            areaSeries : [],
            // 라디알차트 데이터
            radialSeries: [1],
            // 히트차트 데이터
            heatSeries: [
                // {
                //     name: 'Metric1',
                //     data: [{x:'xx'},{x:'xx'},{x:'xx'}]
                // },
                // {
                //     name: 'Metric2',
                //     data: [{x:'yy', y:2}]
                // },
                // {
                //     name: 'Metric3',
                //     data: [{x:'zz', y:2}]
                // },
            ],
            // 스캐터차트 데이터
            scatterSeries: [
                {
                    name: "SAMPLE A",
                    data: [
                        [16.4, 5.4], [21.7, 2], [25.4, 3], [19, 2], [10.9, 1], [13.6, 3.2], [10.9, 7.4], [10.9, 0], [10.9, 8.2], [16.4, 0], [16.4, 1.8], [13.6, 0.3], [13.6, 0], [29.9, 0], [27.1, 2.3], [16.4, 0], [13.6, 3.7], [10.9, 5.2], [16.4, 6.5], [10.9, 0], [24.5, 7.1], [10.9, 0], [8.1, 4.7], [19, 0], [21.7, 1.8], [27.1, 0], [24.5, 0], [27.1, 0], [29.9, 1.5], [27.1, 0.8], [22.1, 2]
                    ]
                }
            ],
            // 바차트 데이터
            barSeries:[       
            ],
            // 파이차트 데이터
            pieSeries: [3, 100],

            chartTitle: 'TPS 차트',

            heatChartTitle : '시간별 응답 현황',

            areaChartTitle : '응답 시간',

            // 라인 차트 옵션
            chartOptions: {
                
                title: {
                    // text: 'TPS',
                    // align: 'center',
                    // style: {
                    //     fontSize:  '18px',
                    //     fontWeight:  'bold',
                    //     fontFamily:  undefined,
                    //     color:  '#000000'
                    // },
                },
                //
                chart: {
                    id: 'chartLine',
                    toolbar: {
                        show: true,
                        tools: {
                            download: true,
                            selection: false,
                            zoom: true,
                            zoomin: false,
                            zoomout: false,
                            pan: false,
                            reset: false | '<img src="/static/icons/reset.png" width="20">',
                            customIcons: []
                        },
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
                        text: 'Minute : Second'
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
            // 컬럼 차트 옵션
            colChartOptions:{
                title: {
                    text: '정보 송신 현황',
                    align: 'center',
                    style: {
                        fontSize:  '18px',
                        fontWeight:  'bold',
                        fontFamily:  undefined,
                        color:  '#000000'
                    },
                },
                xaxis: {
                    categories: [
                        ['정보 송신'],
                    ],
                },
                legend:{
                    horizontalAlign :'center',
                    itemMargin: {
                        //horizontal: 15,
                        vertical: 16
                    },
                    // markers:{
                    //     width:30
                    // }
                }
            },
            //에이리어 차트 옵션
            areaChartOptions: {
                colors: ['#FF0080' ,'#2E93fA', '#66DA26', '#546E7A', '#E91E63', '#FF9800'],
                // title: {
                //     text: '응답 시간',
                //     align: 'center',
                //     style: {
                //         fontSize:  '18px',
                //         fontWeight:  'bold',
                //         fontFamily:  undefined,
                //         color:  '#000000'
                //     },
                // },
                chart: {
                    type: 'area',
                    height: 350,
                    zoom: {
                        enabled: true
                    },
                    toolbar: {
                        show: true,
                        tools: {
                            download: true,
                            selection: false,
                            zoom: true,
                            zoomin: false,
                            zoomout: false,
                            pan: false,
                            reset: false | '<img src="/static/icons/reset.png" width="20">',
                            customIcons: []
                        },
                    },
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    width:1,
                    curve: 'smooth'
                },
                xaxis:{
                    title: {
                        text: 'Second'
                    },
                    labels: {
                        //show: false,
                        show: true,
                        formatter: function(value) {
                            var temp = Math.floor(parseInt(value))-1;
                            // console.log("formatter에서의 arrLabel : ", arrLabel[value])
                            // console.log("zz",value, temp)
                            return areaArrLabel[temp];//ct==category 배열 꼴로 라벨링
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
           
                    //colors:['#585858', '#2E9AFE', '#FA5858'],
                },
                yaxis: {
                    title: {
                        //text: '응답시간(milliseconds)'
                        text: 'Response Time'
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

            // 라디아 차트 옵션
            radialChartOptions: {
                title: {
                    text: '응답률',
                    align: 'center',
                    style: {
                        fontSize:  '18px',
                        fontWeight:  'bold',
                        fontFamily:  undefined,
                        color:  '#000000'
                    },
                },
                
                chart: {
                    height: 350,
                    type: 'radialBar',
                    toolbar: {
                        show: true
                    }
                },
                colors: ["#20E647"],
                plotOptions: {
                    radialBar: {
                        //startAngle: -135,
                        //endAngle: 225,
                        hollow: {
                            margin: 0,
                            size: '70%',
                            background: '#293450',
                            // image: undefined,
                            // imageOffsetX: 0,
                            // imageOffsetY: 0,
                            // position: 'front',
                            // dropShadow: {
                            //     enabled: true,
                            //     top: 3,
                            //     left: 0,
                            //     blur: 4,
                            //     opacity: 0.24
                            // }
                        },
                        track: {
                            // background: '#fff',
                            // strokeWidth: '67%',
                            margin: 0, // margin is in pixels
                            dropShadow: {
                                enabled: true,
                                top: 2,
                                left: 0,
                                blur: 4,
                                opacity: 0.15
                            }
                        },
                    
                        dataLabels: {
                            show: true,
                            name: {
                                offsetY: -10,
                                show: true,
                                color: '#fff',
                                fontSize: '13px'
                            },
                            value: {
                                formatter: function(val) {
                                    return parseInt(val) +"%";
                                },
                                color: '#fff',
                                fontSize: '30px',
                                show: true,
                            }
                        }
                    }
                },
                fill: {
                type: 'gradient',
                gradient: {
                    shade: 'dark',
                    type: 'vertical',
                    shadeIntensity: 0.5,
                    gradientToColors: ['#87D4F9'],
                    // inverseColors: true,
                    // opacityFrom: 1,
                    // opacityTo: 1,
                    stops: [0, 100]
                }
                },
                stroke: {
                lineCap: 'round'
                },
                labels: ['Response rate'],
            },
            //히트 차트 옵션
            heatChartOptions:{
                // title: {
                //     text: '시간별 응답 현황',
                //     align: 'center',
                //     style: {
                //         fontSize:  '18px',
                //         fontWeight:  'bold',
                //         fontFamily:  '' ,
                //         color:  '#000000'
                //     },
                // },

                chart: {
                    toolbar: {
                        show: true,
                    },
                    height: 350,
                    type: 'heatmap',
                },
                dataLabels: {
                    enabled: false
                },
                //colors: ["#0000FF"],
                xaxis:  
                {
                    title: {
                        text: 'Minute : Second'
                    },
                    labels: {
                        show: true,
                        // formatter: function(value) {
                        //     var temp = Math.floor(parseInt(value))-1;
                        //     // console.log("formatter에서의 arrLabel : ", arrLabel[value])
                        //     // console.log("zz",value, temp)
                        //     //return heatArrLabel[temp];//ct==category 배열 꼴로 라벨링
                        //     //return arrLabel[0]
                        //     return value;
                        // },
                    },
                   

                    //type:'numeric',
                },
                

                yaxis:[
                    {
                        axisTicks: {
                            show: false,
                        },
                        axisBorder: {
                            show: true,
                            //color: '#008FFB'
                        },
                        labels: {
                            // style: {
                            //     colors: '#008FFB',
                            // }
                        },
                        title: {
                            text: "Response Count",
                            // style: {
                            //     color: '#008FFB',
                            // }
                        },
                        tooltip: {
                            enabled: false
                        }
                    },
                    {   
                        min:1,
                        max:10,
                        tickAmount : 3,
                        // labels: {
                            
                            
                        //     //show: false,
                            
                        //     show: true,
                        //     formatter: function(value) {
                        //         var temp = Math.floor(parseInt(value))-1;
                        //         //console.log("formatter에서의 arrLabel : ", arrLabel[value])
                        //         //console.log("zz",value, temp)
                        //         return heatYLabel[temp];//ct==category 배열 꼴로 라벨링
                        //         //return arrLabel[0]
                        //     }
                            
                        // },
                        
                    }
                ]
                ,
                
                
            },



            // 스캐터 차트 옵션
            scatterChartOptions:{
                chart: {
                    height: 350,
                    type: 'scatter',
                },
                dataLabels: {
                    enabled: false
                },
                colors: ["#008FFB", "#111111"],
                title: {
                    text: '미응답 시간',
                    align: 'center',
                    style: {
                        fontSize:  '18px',
                        fontWeight:  'bold',
                        fontFamily:  undefined,
                        color:  '#000000'
                    },
                },
                xaxis:{
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
                            return scatterArrLabel[temp];//ct==category 배열 꼴로 라벨링
                            //return arrLabel[0]
                        }
                    },
                    axisBorder: {
                        show: false
                    },
                    axisTicks: {
                        show: true
                    },
                    tooltip: {
                        enabled: false
                    },
                    //(max - min)이 1초단위 표현            input : 1 >> max -min

                    //소수점 둘째자리 아래는 표현이 안됨 ex) 0.01 X
                    //(max - min) * 10이 0.1초단위 표현     input : 0.1 >> (max - min) * 100
                    //(max - min) * 0.1이 10초단위 표현     input : 10 >> (max - min) * 0.1
                    //(max - min) * 0.01이 100초단위 표현     input : 100 >> (max - min) * 0.01
                    
                    //즉, input 입력 시 (1/input)을 곱해주면 됨
                    type: "numeric",
           
                    //colors:['#585858', '#2E9AFE', '#FA5858'],
                
                
                }
                
            },
            //바 차트 옵션
            barChartOptions:{
                title: {
                    text: '시간별 응답 현황',
                    align: 'center',
                    style: {
                        fontSize:  '18px',
                        fontWeight:  'bold',
                        fontFamily:  undefined,
                        color:  '#000000'
                    },
                },
                color:['#DF0174', '#2EFE2E'],
                chart: {
                    type: 'bar',
                    height: 350,
                    stacked: true,
                    toolbar: {
                        show: true
                    },
                    zoom: {
                        enabled: true
                    }
                },
                responsive: [{
                    breakpoint: 480,
                    options: {
                        legend: {
                        position: 'bottom',
                        offsetX: -10,
                        offsetY: 0
                        }
                    }
                }],
                plotOptions: {
                    bar: {
                        horizontal: false,
                    },
                },
                xaxis: {
                    type: 'category',
                    categories: [],
                },
                legend: {
                    position: 'right',
                    offsetY: 40,
                    inverseOrder: true,
                },
                fill: {
                    opacity: 1
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
            pageSize: 4,
            pagePosition: 'bottom',

            statisticArr: [],
            multipleVal: 0.01,
        }
    },

    created() {
        this.loadGrid()
    },
    // mounted ()  {
    //     this.loadGrid()
    // },
    updated(){
        // this.loadGrid()
        //this.historyClick()
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
                //console.log("timeout 찾기..",response.data.historyList)//
                this.statisticArr = response.data.historyList

                this.data = response.data.historyList
                this.$refs.historyList.selectRow(this.data[this.focusIndex])
            }).catch(ex => {
                console.log(ex)
                this.loading = false
            })
            
        },
        
        xaxisChange(){

            const clonedeep = require('lodash.clonedeep');
            const deepCopied = clonedeep(arrLabel) 
            const heatArrLabelC = arrLabel
            arrLabel = deepCopied

            var multipleVal = this.value
            
            //console.log("zz",multipleVal)
            var tickTemp = (arrLabel.length)*parseFloat(multipleVal)//곱해줄 값을 input 값으로 받자! select 해서 받도록 단위 1초(1), 10초(0.1), 60초(0.6), 3600초(0.00027) 
            //console.log("max-min",(arrLabel.length-1))
            //console.log("parseFloat(multipleVal)",parseFloat(multipleVal))
            //console.log("tickTemp",tickTemp)
            //console.log(tickTemp.toFixed(6))
            //console.log("arrLabel 변경??, deepCopied",arrLabel, deepCopied)


            this.$refs.lineChart.updateOptions({
                xaxis: {
                    //categories: categories
                    min: 1,
                    max: arrLabel.length,
                    tickAmount: tickTemp
                },
            })
 
        },

        heatXaxisChange(){
            
            var multipleVal2 = this.value2
            //alert(multipleVal+"!!")
            //console.log("zz",multipleVal)
            
            //var tickTemp = (heatArrLabel.length)*parseFloat(multipleVal2)//곱해줄 값을 input 값으로 받자! select 해서 받도록 단위 1초(1), 10초(0.1), 60초(0.6), 3600초(0.00027) 
            
            //console.log("max-min",(arrLabel.length-1))
            //console.log("parseFloat(multipleVal)",parseFloat(multipleVal))
            //console.log("tickTemp",tickTemp)
            //console.log(tickTemp.toFixed(6))


            var tickArr = []//입력할 라벨
            //var src = heatArrLabel//원본
            //var des = src//변경될 사본
            const clonedeep = require('lodash.clonedeep');
            const deepCopied = clonedeep(arrLabel) 

            
            //console.log("바뀔 배열 ,deepCopied",arrLabel, deepCopied)

            if(multipleVal2 == 1){
                
            }
            else if(multipleVal2 == 10){
                
                for(var i=0; i<arrLabel.length; i++){
                    if(i!=0 && i%10 != 9){
                        delete arrLabel[i]
                    }
                }

                tickArr = arrLabel
                
                //console.log("변하는배열, deepCopied ",arrLabel,deepCopied)
                arrLabel = deepCopied
            }
            else if(multipleVal2 == 60){
                for(var i=0; i<arrLabel.length; i++){
                    if(i!=0 && i%60 != 59){
                        delete arrLabel[i]
                    }
                }

                tickArr = arrLabel
                
                arrLabel = deepCopied
            }
            else if(multipleVal2 == 3600){
                for(var i=0; i<arrLabel.length; i++){
                    if(i!=0 && i%3600 != 3599){
                        delete arrLabel[i]
                    }
                }

                tickArr = arrLabel
                
                arrLabel = deepCopied
            }



            this.$refs.heatChart.updateOptions({
                xaxis: {
                    
                    //categories: ['x2','x3','x4','x5'],
                    categories: tickArr
                    // min: 1,
                    // max: heatArrLabel.length,
                    // tickAmount: tickTemp
                },
            })
        },


        historyClick(row) {
            this.value=1
            this.value2=1
            this.multipleVal=0.01
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
                    //console.log("값 오나..",response.data.heatGraphList)

                    var resultList = response.data.resultList
                    var graphList = response.data.graphList
                    var heatGraphList = response.data.heatGraphList

                    var heatGraphListLen = heatGraphList.length
                    this.logTotalCount = resultList.length
                    this.logData = resultList
                    this.logLoading = false

                    //park excel 메소드 참고
                    var start
                    var end
                    var maxResTime =0;
                    var minResTime;
                    var timeout = []
                    var resTime = []

                    var areaCategories=[]
                    var scatterCategories=[]
                    var i=0
 
                    
                    //초당 성공/실패 횟수 저장변수
                    var secMark=0
                    var secMove=0
                    var secSuccess=0
                    var secFail=0
                    var secSuccessArr = []
                    var secFailArr = []
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
                        //응답시간들
                        resTime.push(value.resTime)
                        areaCategories.push(value.startDatetime.substring(17,23))
                        scatterCategories.push(value.startDatetime.substring(17,23))
                        // //초 단위마다의 성공, 실패 횟수 저장
                        // if(index == 0){
                        //     secMark=value.startDatetime.substring(17,19)//첫번째 시작시간의 1초단위 저장 - 기준점
                        //     //console.log("secMark",secMark)
                        // }
                        // secMove=value.startDatetime.substring(17,19)//1초단위 바뀌는 값
                        // //startDateTime의 18번이 바뀔 때마다!
                        // if(secMove!=secMark){
                        //     //console.log(secMove)

                        //     secSuccessArr.push(secSuccess)
                        //     secFailArr.push(secFail)
                        //     secSuccess = 0
                        //     secFail = 0
                        //     if(value.rstType == "SUCCESS"){
                        //         secSuccess++
                        //     }else if(value.rstType == "FAIL"){
                        //         secFail++
                        //     }
                        //     secMark = secMove
                        // }else{
                        //     //console.log(secMove)
                            
                        //     if(value.rstType == "SUCCESS"){
                        //         secSuccess++
                        //         if(index == resultList.length-1){
                        //             secSuccessArr.push(secSuccess)
                        //             secFailArr.push(secFail)
                        //         }
                        //     }else if(value.rstType == "FAIL"){
                        //         secFail++
                        //         if(index == resultList.length-1){
                        //             secSuccessArr.push(secSuccess)
                        //             secFailArr.push(secFail)
                        //         }
                        //     }
                        // }

                        //x축 설정할 값
                        // var secString = value.endDatetime
                        // secString.substring(16,19)
                        
                        // if(index == (this.logTotalCount / graphList.length)*i)
                        //     areaCategories.push()
                        // i++
                        
                        //타임아웃저장
                        //timeout.push(value.timeout)
                    })
                    //console.log("timout 옮겨갔는지 확인",this.statisticArr)//확인 -> 모든 실행목록임 매칭시켜야함
                    
                    // console.log("초당 성공 시간들 ",secSuccessArr)
                    // console.log("초당 실패 시간들 ",secFailArr)

                    // console.log("응답시간들!!",resTime)
                    // //console.log("타임아웃들!", timeout)

                    // console.log('시작 시간:'+  start)
                    // console.log('종료 시간:'+  end)
                    // console.log('최대 응답 시간:'+  maxResTime)
                    // console.log('최소 응답 시간:'+  minResTime)
                    // console.log('전체 수행 횟수:'+  this.logData.length)
                    // console.log('성공 횟수:'+  this.statisticArr[this.focusIndex].successCount)
                    // console.log('실패 횟수:'+  this.statisticArr[this.focusIndex].failCount)
                    
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
                    //console.log("내 라벨", arrLabel)
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

                    //console.log(series)
                    //console.log(sumEndRequest)
                    //console.log(graphList.length)
                    this.$refs.lineChart.updateSeries(series)
                    this.chartTitle = '평균 TPS: ' + Math.round((sumEndRequest / graphList.length))

                    
                    //*/
                    /*********************************************컬럼 차트 데이터 가공************************************************************/
                    var colSeries = []
                    var wholeData = []
                    var successCount = []
                    var failCount = []
                    wholeData.push(this.logData.length)
                    successCount.push(this.statisticArr[this.focusIndex].successCount)
                    failCount.push(this.statisticArr[this.focusIndex].failCount)
                    //console.log("성공수!! 실패수!!",successCount,failCount)


                    // colSeries.push({
                    //     name: ['전체 수행 횟수'],
                    //     data: [this.logData.length ,this.statisticArr[this.focusIndex].successCount,this.statisticArr[this.focusIndex].failCount],
                    //     colors:['#F44336', '#E91E63', '#9C27B0']
                        
                    // })
                   
                    colSeries.push({
                        name: ['전체 수행 횟수'],
                        data: wholeData,                        
                    })
                    colSeries.push({
                        name: ['성공 횟수'],
                        data: successCount,
                    })
                    colSeries.push({
                        name: ['실패 횟수'],
                        data: failCount,
                    })

                    this.$refs.colChart.updateOptions({
                        colors:['#585858', '#2E9AFE', '#FA5858']
                    })
                    

                    this.$refs.colChart.updateSeries(colSeries)
                    /*************************************************** 라디알 차트 데이터 가공 *****************************************************/
                    var resRate = Number(this.statisticArr[this.focusIndex].successCount)/Number(this.logData.length) * 100
                    this.radialSeries.pop()
                    this.radialSeries.push(resRate)


                    /*************************************************** 에이리어 차트 데이터 가공 *****************************************************/
                    var areaSeries = []//응답시간 데이터
                    //var multipleVal = 0.01
                    var tickTemp = (areaArrLabel.length)*parseFloat(this.multipleVal)//곱해줄 값을 input 값으로 받자! select 해서 받도록 단위 1초(1), 10초(0.1), 60초(0.6), 3600초(0.00027) 
                   
                    areaSeries.push({
                        name: ['응답시간(ms)'],
                        data: resTime,                        
                    })

                    // areaSeries.push({
                    //     name: ['타임아웃'],
                    //     data: timeout,                        
                    // })
                   
                    //console.log("zzzzzdfsdf",areaCategories)
                    areaArrLabel = areaCategories
                    this.$refs.areaChart.updateOptions({
                        //colors:['#585858', '#2E9AFE', '#FA5858'],
                        xaxis: {
                            min: 1,
                            max: areaArrLabel.length,
                            //tickAmount: tickTemp,//항상 100개단위로 끊는게 아님.. 
                            tickAmount: (areaArrLabel.length-1)*0.01
                        },
                    })
                    

                    this.$refs.areaChart.updateSeries(areaSeries)
                    /*************************************************** 바 차트 데이터 가공 *****************************************************/
                    // var barSeries = []
                    
                    // barSeries.push(
                    //     {

                    //         name: ['미응답'],
                    //         data: secFailArr,                        
                    //     }
                    // )
                     
                    // barSeries.push(
                    //     {

                    //         name: ['응답완료'],
                    //         data: secSuccessArr,                        
                    //     }
                    // )

                    // this.$refs.barChart.updateOptions({
                    //     colors:['#F78181', '#00E396',],
                    //     xaxis: {
                         
                    //         categories:arrLabel,
    
                    //     },
                    // })
                    // this.$refs.barChart.updateSeries(barSeries)
                    /*************************************************** 히트 차트 데이터 가공 *****************************************************/
                    var heatSeries = []

                    var tempDataArr=[]
                    var tempData={x:1,y:2}

                    var secMark
                    var secMove
                    
                    //console.log()

                    //console.log("asdf",heatGraphList[0].MIN_SEC)
                    // for(var i=0; i<heatGraphList.length; i++){
                    //     //초기설정
                    //     if(i==0){
                    //         secMark = heatGraphList[i].MIN_SEC 
                    //     }
                    //     secMove = heatGraphList[i].MIN_SEC

                    //     //같은 초일때 y가 증가해야함
                    //     if(secMove==secMark){
                    //         tempDataArr.push(
                    //             {
                    //                 name: secMove,
                    //                 data:tempDataArr
                    //             }
                    //         )
                    //     }
                    //     //바뀔 때 push!
                    //     else{

                    //     }

                    // }
                    

                    // //x축 개수
                    // for(var i=0; i<5; i++){
                    //     tempDataArr.push(tempData)
                    // }
                    // //y축 개수
                   
                    // for(var i=0; i<10; i++){
                    //     heatSeries.push(
                    //         {
                    //             name:'hello',
                    //             data:tempDataArr
                    //         }
                    //     )
                    // }


                    ////////////////////////////////////////////////////////////////////////////////

                    // var maxValue = 0
                    // var secData = []//
                    // var heatCategories = []
                    
                    // var sData = {}
                    // var mData = []
                    // for(var i=0; i<heatGraphListLen; i++){
                    //     //초기설정
                    //     if(i==0){
                    //         secMark = heatGraphList[i].MIN_SEC 
                    //     }
                    //     secMove = heatGraphList[i].MIN_SEC

                    //     //아직 같은 초 라면
                    //     if(secMove == secMark){
                    //         sData = {x:secMove, y:heatGraphList[i].rowNum}
                    //         mData.push(sData)
                    //     }
                    //     //초가 바뀌었다면
                    //     else{

                    //     }    
                       
                    //     sData = {x:heatGraphList.MIN_SEC }
                        
                    //     heatCategories.push(heatGraphList[i-1].MIN_SEC)

                    //     if(i==heatGraphList.length-1){
                    //         // heatSeries.push(
                    //         //     {
                    //         //         name: secMark,
                    //         //         data:secData
                    //         //     }
                    //         // )
                    //         heatCategories.push(heatGraphList[i].MIN_SEC)
                    //     }
                    // }
                    //console.log("y축",heatSeries)
                    //console.log("x축",heatCategories)
                    /**********************x,y 직접 바꾸기 전***********************/
                    // var maxValue = 0
                    // var secData = []//초당 데이터
                    // var secDataForm = {}
                    // for(var i=0; i<heatGraphListLen; i++){
                    //     //초기설정
                    //     if(i==0){
                    //         secMark = heatGraphList[i].MIN_SEC 
                    //     }
                    //     secMove = heatGraphList[i].MIN_SEC
                        
                    //     //아직 같은 초 라면
                    //     if(secMove == secMark){
                    //         secData.push(heatGraphList[i].rowNum)// << 원본
                    //     }
                    //     //초가 바뀌었다면
                    //     else{
                    //         if(maxValue<heatGraphList[i-1].rowNum){
                    //             maxValue = heatGraphList[i-1].rowNum
                    //         }
                            
                    //         heatSeries.push(
                    //             {
                    //                 name: secMark, //1,2,3...?
                    //                 data:secData//여기에 일단 push할 값 생각해보자 << secData
                    //             }
                    //         )
                            
                    //         secMark = secMove
                    //         secData = []
                    //         secData.push(heatGraphList[i].rowNum)
                    //     }

                    //     if(i==heatGraphList.length-1){
                    //         heatSeries.push(
                    //             {
                    //                 name: secMark,
                    //                 data:secData
                    //             }
                    //         )
                    //     }
                    // }
                    /**********************x,y 직접 바꾸기 전***********************/

                    // var heatCategories = []
                    // for(var i=0; i<maxValue; i++){
                    //     heatCategories.push(i+1)
                    // }

                    //heatChartOptions.plotOptions.heatmap.colorScale.inverse = true
                    var prevRowNum = 1;
                    var skipIndex = 0;

                    var tempArray = Array();

                    //열 별로 묶기 ex) 35:14초 - 2개, 35:15초 - 3개 ...
                    let group = heatGraphList.reduce((r, a) => {
                        r[a.MIN_SEC] = [...r[a.MIN_SEC] || [], a];
                        return r;
                    }, {});
                    //console.log("group", group);
                    
                    //console.log("Length... : ",heatGraphList.length);

                    for (var i=0; i<arrLabel.length; i++){
                        for (var j=0; j<3; j++){
                            var findObj = group[arrLabel[i]].filter(function(item) {
                                return item.rowNum === j
                            });

                            //console.log("Find object : ", findObj);
                        }
                    }


                    ///최대 세로 개수 구하기
                    var maxRows = 0;
                    for (var i=0; i<arrLabel.length; i++){
                        if (group[arrLabel[i]].length > maxRows){
                            maxRows = group[arrLabel[i]].length;
                        }
                    }

                    var tempArray = new Array();
                    var maxRowNum = maxRows;
                    
                    for (var x=1; x<maxRowNum+1; x++){
                        for (var i=0; i<arrLabel.length; i++){
                            var findObj = group[arrLabel[i]].filter(function(item) {
                                return item.rowNum === x
                            });
                            
                            if (findObj.length == 0){
                                // 공백 처리
                                var temp = {"x" : arrLabel[i], "y" : -1};
                            }else{
                                // push
                                var temp = {"x" : arrLabel[i], "y" : findObj[0]["RST_TYPES"]};
                            }
                            
                            tempArray.push(temp);
                        }
                        
                        //console.log(tempArray);
                        
                        heatSeries.push(
                            {
                                name: x+"번째 데이터",
                                data: tempArray
                            }
                        )
                        
                        tempArray = new Array();
                    }
                    ///


                    //  for (var i=0; i<heatGraphList.length; i++) {
                    //     if (i != 0){
                    //         if (heatGraphList[i].rowNum == heatGraphList[i-1].rowNum){
                    //             var temp = {"x" : heatGraphList[i].MIN_SEC, "y" : Number(heatGraphList[i].RST_TYPES)};
                    //             tempArray.push(temp);
                    //         }else{

                    //             console.log("prevRowNum : ", heatGraphList[i-1].rowNum);
                    //             console.log("tempArray : ", tempArray);

                    //             heatSeries.push(
                    //                 {
                    //                     name: heatGraphList[i-1].rowNum,
                    //                     data: tempArray
                    //                 }
                    //             )

                    //             tempArray = Array(0);

                    //             var temp = {"x" : heatGraphList[i].MIN_SEC, "y" : Number(heatGraphList[i].RST_TYPES)};
                    //             tempArray.push(temp);

                    //             if (i == heatGraphList.length-1){
                    //                 heatSeries.push(
                    //                     {
                    //                         name: heatGraphList[i].rowNum,
                    //                         data: tempArray
                    //                     }
                    //                 )
                    //             }
                    //         }
                    //     }else{
                    //         var temp = {"x" : heatGraphList[i].MIN_SEC, "y" : Number(heatGraphList[i].RST_TYPES)};
                    //         tempArray.push(temp);
                    //     }





                        // while (true){
                        //     if (heatGraphList[i].rowNum == prevRowNum) {
                        //         var temp = {"x" : heatGraphList[i].MIN_SEC, "y" : Number(heatGraphList[i].RST_TYPES)};
                        //         tempArray.push(temp);
                        //         i++;
                        //     }
                        //     else{
                        //         prevRowNum = heatGraphList[i].rowNum;
                        //         break;
                        //     }
                        // }

                        // console.log("prevRowNum : ", prevRowNum);
                        // console.log("tempArray : ", tempArray);
                        
                        // heatSeries.push(
                        //     {
                        //         name: prevRowNum,
                        //         data: tempArray
                        //     }
                        // )
                        
                    // }
                                        

                    for(var i=0; i< heatSeries.length; i++){
                         heatYLabel.push(heatSeries[i].name)
                    }
                    
                    //heatArrLabel = tempLabelArr

                    this.$refs.heatChart.updateOptions({
                        colors:['#2E9AFE', ],
                        
                        xaxis: {
                            categories: arrLabel,
                            // min: 0,
                            // max: heatArrLabel.length-1,
                            // tickAmount: heatArrLabel.length-1,

                            tooltip: {
                                enabled: true,
                                //x:"hello"
                                // formatter: function (value) {
                                //     return '성공 '+ value
                                // }
                            }
                        },
                        yaxis: [
                            {
                                axisTicks: {
                                    show: false,
                                },
                                axisBorder: {
                                    show: true,
                                    //color: '#008FFB'
                                },
                                labels: {
                                    show: false,
                                    style: {
                                        colors: '#008FFB',
                                    }
                                },
                                // title: {
                                //     text: "Income (thousand crores)",
                                //     style: {
                                //         color: '#008FFB',
                                //     }
                                // },
                                
                            },
                            {
                                min: 0,
                                max: heatYLabel.length,
                                //tickAmount:(heatYLabel.length)*0.1,
                                tickAmount:(heatYLabel.length)/10,

                                // min:0,
                                // max:100,
                                // tickAmount : 100*0.1,
                                title: {
                                    text: "Response Count",
                                    // style: {
                                    //     color: '#008FFB',
                                    // }
                                },

                            }
                        ],
                        
                        tooltip: {
                            custom: function(opts) {
                                const value = opts.series[opts.seriesIndex][opts.dataPointIndex]
                                
                                
                                // if (String(value) == "1"){
                                //     return ('<div class="arrow_box">' + '<label>hello</label>'+
                                // "<span>" +(opts.seriesIndex+1)+"번째 데이터 : 성공" +"</span>" +
                                // "</div>");
                                // }
                                
                                if (String(value) == "1"){
                                    return (opts.seriesIndex+1)+"번째 데이터 : 성공";
                                }
                                else if (String(value) == "0"){
                                    return (opts.seriesIndex+1)+"번째 데이터 : 실패";
                                }
                                else if (String(value) == "-1"){
                                    return "Empty";
                                }
                                else
                                {
                                    return "";
                                }
                            },
                            // title: {
                            //     formatter: function (seriesName) {
                            //     return 'hello'
                            //     }
                            // }
                        },
                        
                        plotOptions: {
                            heatmap: {
                                colorScale: {
                                    ranges: [
                                        {
                                            from: -1000,
                                            to: -1,
                                            color: '#FFFFFF',
                                            name: '  ',
                                        },
                                        {
                                            from: 1,
                                            to: 1,
                                            color: '#58ACFA',
                                            name: '성공',
                                        },
                                        {
                                            from: 0,
                                            to: 0,
                                            color: '#ff0000',
                                            name: '실패',
                                        },
                                        
                                    ]
                                }
                            }
                        }
                    })
                    this.$refs.heatChart.updateSeries(heatSeries)
                    heatSeries = []
                    heatYLabel = []
                    //heatCategories = []
                    //heatChart.$forceUpdate();


                    /*************************************************** 스캐터 차트 데이터 가공 *****************************************************/
                    // var scatterSeries = []
                    
                    // //var lData = {name:"hihi", data:[[1,2],[2,4],[3,4]]}
                    // var lData = {}
                    // var mData = []
                    // var sData = []

                    // scatterArrLabel = scatterCategories
                    // //console.log("스캐터라벨", scatterArrLabel)

                    // for(var i=0; i<scatterArrLabel.length; i++){
                    //     // sData[1,625ms]
                    //     sData.push(i+1)
                    //     sData.push(resTime[i])
                        
                    //     //mData = [[],[],[]]
                    //     //mData.push(sData)
                    //     mData[i] = sData

                    //     sData = []
                    // }
                   
                    // lData = {name:'hihi', data:mData}
                
                    // scatterSeries.push(lData)
                    
                    // this.$refs.areaChart.updateOptions({
                    //     //colors:['#585858', '#2E9AFE', '#FA5858'],
                    //     xaxis: {
                    //         min: 1,
                    //         max: scatterArrLabel.length,
                    //         //tickAmount: tickTemp,//항상 100개단위로 끊는게 아님.. 
                    //         tickAmount: (scatterArrLabel.length-1)*0.01
                    //     },
                    // })

                    // this.$refs.scatterChart.updateSeries(scatterSeries)
                    

                    
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

/***************/
.arrow_box {
  position: relative;
  background: #555;
  border: 2px solid #000000;
}
.arrow_box:after, .arrow_box:before {
  right: 100%;
  top: 50%;
  border: solid transparent;
  content: " ";
  height: 0;
  width: 0;
  position: absolute;
  pointer-events: none;
}

.arrow_box:after {
  border-color: rgba(85, 85, 85, 0);
  border-right-color: #555;
  border-width: 10px;
  margin-top: -10px;
}
.arrow_box:before {
  border-color: rgba(0, 0, 0, 0);
  border-right-color: #000000;
  border-width: 13px;
  margin-top: -13px;
}

#chart .apexcharts-tooltip {
  color: #fff;
  transform: translateX(10px) translateY(10px);
  overflow: visible !important;
  white-space: normal !important;
}

#chart .apexcharts-tooltip span {
  padding: 5px 10px;
  display: inline-block;
}
</style>