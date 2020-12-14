webpackJsonp([5],{"2GhO":function(t,e){},QlRo:function(t,e,o){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var n=o("SDIm"),i=o("RKAd"),a=o.n(i),s=o("uXZL"),l=o.n(s),r={components:{Buttons:n.a,VueJsonPretty:a.a},data:function(){var t=this;return{jsonData:{},htmlData:"",resultContent:!1,buttonEvents:{refresh:{show:!0,event:function(){t.loadGrid()}},download:{show:!0,event:this.downloadExcel}},series:[{data:[]}],testSeries:[{data:[]}],pieSeries:[0,0],chartTitle:"TPS 차트",chartOptions:{series:[],chart:{id:"chartLine",toolbar:{show:!1},zoom:{type:"x",enabled:!0,autoScaleYaxis:!0}},stroke:{width:3},markers:{size:0,strokeWidth:2},legend:{position:"top"},xaxis:{title:{text:"Second"},labels:{show:!0,hideOverlappingLabels:!0},axisBorder:{show:!1},axisTicks:{show:!1},tooltip:{enabled:!1},tickAmount:4},yaxis:{title:{text:"TPS Count"},tickAmount:4,min:0}},pieChartOptions:{title:{text:"SUCCESS/FAIL 차트",align:"center"},colors:["#00E397","#FF7979"],labels:["SUCCESS","FAIL"],legend:{position:"top"}},focusIndex:0,data:[],loading:!1,logData:[],logLoading:!1,logTotalCount:0,pageSize:50,pagePosition:"bottom",xmax:0,xmin:0,xtick:0}},created:function(){this.loadGrid()},methods:{loadGrid:function(){var t=this;this.loading=!0,this.axios.get("/api/v1/statistics").then(function(e){t.loading=!1,e.data.historyList.forEach(function(t,e){t.index=e}),t.data=e.data.historyList,t.$refs.historyList.selectRow(t.data[t.focusIndex])}).catch(function(e){console.log(e),t.loading=!1})},historyClick:function(t){var e=this;if(t){this.focusIndex=t.index,this.logLoading=!0;var o=this.$loading({lock:!0,text:"Data Loading...",spinner:"el-icon-loading",target:this.$refs.container.$el,fullscreen:!1});this.axios.get("/api/v1/statistics/result",{params:{workSeq:t.workSeq}}).then(function(t){o.close();var n=t.data.resultList,i=t.data.graphList;e.logTotalCount=n.length,e.logData=n,e.logLoading=!1;var a=[],s=[],l=[],r=0,d=[],h=[];i.forEach(function(t,o,n){a.push(t.resultTime),h.push(t.resultTime),console.log("총길이 : "+n.length),0==o?e.xmin=t.resultTime:o==n.length-1&&(e.xmax=t.resultTime),console.log("hello ["+o+"] "+t.resultTime),s.push(t.countStartRequest),l.push(t.countEndRequest),r+=t.countEndRequest}),e.xtick=e.xmax<e.xmin?Number(60)+Number(e.xmax)-e.xmin:e.xmax-e.xmin,console.log("끝 : "+e.xmin,e.xmax,e.xtick),console.log("카테고리 : "+a),console.log("시리즈 : "+h),h.push({name:"초",data:h}),e.$refs.lineChart.updateOptions({xaxis:{labels:a}}),d.push({name:"요청수",data:s}),d.push({name:"응답수",data:l}),e.$refs.lineChart.updateSeries(d),e.chartTitle="평균 TPS: "+Math.round(r/i.length)}).catch(function(t){e.logLoading=!1,o.close(),console.log(t)})}},resTimeCellCss:function(t,e){if("FAIL"==t.rstType)return{background:"#FF7979"}},dbClickResult:function(t){this.resultContent=!0;try{this.jsonData=JSON.parse(t.rstContent)}catch(e){this.jsonData=t.rstContent}},downloadExcel:function(){if(this.logLoading)this.notification("downloadExcel");else{var t=[];this.logData.forEach(function(e,o){t.push({No:o+1,"수행명":e.rstName,"수행일시":"'"+e.startDatetime,"수행종료일시":"'"+e.endDatetime,"응답시간(ms)":e.resTime,"내용":e.rstContent})});var e=l.a.utils.book_new(),o=l.a.utils.json_to_sheet(t);l.a.utils.book_append_sheet(e,o,"Sheet1"),l.a.writeFile(e,"log.csv")}}}},d={render:function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("div",[o("Layout",{staticStyle:{width:"100%",height:"800px"}},[o("LayoutPanel",{staticStyle:{height:"45px","text-align":"right"},attrs:{region:"north",border:!1}},[o("Buttons",{attrs:{buttonEvents:t.buttonEvents}})],1),t._v(" "),o("LayoutPanel",{staticStyle:{width:"400px"},attrs:{region:"west",title:"수행기록"}},[o("DataGrid",{ref:"historyList",attrs:{data:t.data,columnResizing:!0,border:!1,loading:t.loading,selectionMode:"single"},on:{selectionChange:function(e){return t.historyClick(e)}}},[o("GridColumn",{attrs:{cellCss:"datagrid-td-rownumber",width:"30",align:"center"},scopedSlots:t._u([{key:"body",fn:function(e){return[t._v("\n                        "+t._s(e.rowIndex+1)+"\n                    ")]}}])},[o("template",{slot:"header"},[t._v("\n                        No.\n                    ")])],2),t._v(" "),o("GridColumn",{attrs:{field:"workDatetime",title:"수행일시",width:"160",align:"center",sortable:""}}),t._v(" "),o("GridColumn",{attrs:{field:"workName",title:"수행명",align:"left",sortable:""}}),t._v(" "),o("GridColumn",{attrs:{field:"rstCount",title:"수행건수",align:"center",width:"70",sortable:""}})],1)],1),t._v(" "),o("LayoutPanel",{staticStyle:{width:"100%",height:"100%"},attrs:{region:"center",border:!1}},[o("Layout",{ref:"container"},[o("LayoutPanel",{staticStyle:{width:"100%",height:"380px"},attrs:{region:"north",title:"수행결과"}},[t._v("\n                    graph1\n                    \n                ")]),t._v(" "),o("LayoutPanel",{staticStyle:{width:"100%",height:"380px"},attrs:{region:"west"}},[t._v("\n                    graph2\n                ")])],1)],1)],1),t._v(" "),o("el-dialog",{attrs:{title:"결과 내용",visible:t.resultContent,width:"800px","custom-class":"dialog-h"},on:{"update:visible":function(e){t.resultContent=e}}},[o("vue-json-pretty",{attrs:{data:t.jsonData,showLength:!0,showDoubleQuotes:!1,showSelectController:!0,highlightMouseoverNode:!0}})],1)],1)},staticRenderFns:[]};var h=o("VU/8")(r,d,!1,function(t){o("2GhO")},"data-v-63c556f5",null);e.default=h.exports}});
//# sourceMappingURL=5.4718a7929411590b07aa.js.map