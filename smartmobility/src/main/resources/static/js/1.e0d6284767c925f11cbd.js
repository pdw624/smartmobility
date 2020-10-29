webpackJsonp([1],{QC2H:function(e,t){},qTYp:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=n("woOf"),o=n.n(i),a=n("Dd8w"),r=n.n(a),c=n("NYxO"),s={components:{Buttons:n("SDIm").a},data:function(){return{buttonEvents:{refresh:{show:!0,event:this.loadGrid},add:{show:!0,event:this.addScenario},delete:{show:!0,event:this.deleteScenario},execute:{show:!0,event:this.executeScenario},pause:{show:!0,event:this.pauseScenario}},scenarioForm:{scenarioName:"",userCount:1,loopCount:1},unRegisterAction:[],registerAction:[],rules:{scenarioName:[{required:!0,message:"시나리오명을 입력해주세요",trigger:"blur"}],userCount:[{required:!0,message:"유저수를 입력해주세요",trigger:"blur"}],loopCount:[{required:!0,message:"반복횟수를 입력해주세요",trigger:"blur"}]},data:[],deletedScenarioData:[],allChecked:!1,rowClicked:!1,isUpdate:!0,loading:!1,focusIndex:0,dialogDeletedScenario:!1,deletedLoading:!1}},mounted:function(){this.loadGrid()},computed:{checkedRows:function(){return this.data.filter(function(e){return e.selected})}},methods:r()({},Object(c.c)(["setIsExecute"]),{loadGrid:function(e){var t=this;this.loading=!0,this.axios.get("/api/v1/scenario").then(function(n){var i;(t.loading=!1,n.data.scenarioList.forEach(function(e,t){e.index=t}),t.data=n.data.scenarioList,t.unRegisterAction=n.data.actionList,0==t.data.length)?(t.isUpdate=!1,t.allChecked=!1,t.resetScenarioForm()):(i=e?t.data.length-1:t.focusIndex,t.$refs.scenarioList.selectRow(t.data[i]))}).catch(function(e){t.loading=!1,console.log(e)})},loadDeletedGrid:function(){var e=this;this.axios.get("/api/v1/scenario/deleted").then(function(t){e.deletedScenarioData=t.data.deletedScenarioList,e.dialogDeletedScenario=!0}).catch(function(e){console.log(e)})},onAllCheckedChange:function(e){this.rowClicked||(this.data=this.data.map(function(t){return o()({},t,{selected:e})}))},onCheckedChange:function(e){var t=this;this.allChecked=this.checkedRows.length===this.data.length,this.rowClicked=!0,this.$nextTick(function(){return t.rowClicked=!1})},resetScenarioForm:function(){this.$refs.scenarioForm.resetFields(),this.registerAction=[]},scenarioClick:function(e){if(this.isUpdate=!0,e){this.focusIndex=e.index;var t=o()({},e);this.scenarioForm=t;for(var n=[],i=0;i<e.actionList.length;i++)n.push(e.actionList[i].actionId);this.registerAction=n}},saveScenario:function(e){var t=this;this.$refs[e].validate(function(e){if(!e)return!1;if(0==t.registerAction.length)return t.notification("saveScenarioCheck"),!1;var n={scenario:{scenarioName:t.scenarioForm.scenarioName,loopCount:t.scenarioForm.loopCount,userCount:t.scenarioForm.userCount},actionIds:t.registerAction};t.isUpdate?(n.scenario.scenarioId=t.scenarioForm.scenarioId,t.axios.put("/api/v1/scenario",n).then(function(e){t.loadGrid(),t.notification("saveScenario")})):t.axios.post("/api/v1/scenario",n).then(function(e){t.loadGrid(!0),t.notification("saveScenario")})})},addScenario:function(){var e=this;this.resetScenarioForm(),this.$refs.scenarioList.clearSelections(),this.$nextTick(function(){return e.isUpdate=!1})},deleteScenario:function(){var e=this;0==this.checkedRows.length?this.notification("deleteScenarioCheck"):this.$confirm("체크한 시나리오들을 삭제하시겠습니까?",{confirmButtonText:"삭제",cancelButtonText:"취소",type:"warning"}).then(function(){var t=[];e.checkedRows.forEach(function(e){return t.push(e.scenarioId)}),e.axios.delete("/api/v1/scenario",{data:{scenarioIds:t}}).then(function(t){t.data&&(e.loadGrid(!0),e.notification("deleteScenario"))}).catch(function(t){e.notification("deleteScenarioError")})})},executeScenario:function(){0==this.checkedRows.length?this.notification("executeScenarioCheck"):(this.setIsExecute({isExecute:!0,message:"시나리오가 실행중입니다"}),this.axios.post("/api/v1/scenario/execute",{scenarioList:this.checkedRows}).then(function(e){}))},pauseScenario:function(){var e=this;this.axios.post("/api/v1/scenario/pause").then(function(t){e.setIsExecute({isExecute:!1})}).catch(function(t){e.setIsExecute({isExecute:!1}),console.log(t)})},showDeletedScenario:function(){this.loadDeletedGrid()},recoveryScenario:function(e){var t=this;this.$confirm("시나리오를 복원하시겠습니까?","시나리오복원",{confirmButtonText:"복원",cancelButtonText:"취소",type:"info"}).then(function(){t.axios.post("/api/v1/scenario/recovery",{scenarioIds:[e.scenarioId]}).then(function(e){t.loadGrid(),t.loadDeletedGrid()}).catch(function(){})})}})},l={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("Layout",{staticStyle:{width:"100%",height:"800px"}},[n("LayoutPanel",{staticStyle:{height:"45px","text-align":"right"},attrs:{region:"north",border:!1}},[n("Buttons",{attrs:{buttonEvents:e.buttonEvents}})],1),e._v(" "),n("LayoutPanel",{staticStyle:{width:"450px"},attrs:{region:"west",title:"시나리오 목록"}},[n("template",{slot:"header"},[n("div",{staticClass:"f-row"},[n("div",{staticClass:"panel-title f-full"},[e._v("시나리오 목록")]),e._v(" "),n("LinkButton",{on:{click:function(t){return e.showDeletedScenario()}}},[e._v("삭제된 시나리오")])],1)]),e._v(" "),n("DataGrid",{ref:"scenarioList",staticStyle:{width:"100%",height:"100%"},attrs:{data:e.data,columnResizing:!0,border:!1,loading:e.loading,selectionMode:"single"},on:{selectionChange:function(t){return e.scenarioClick(t)}}},[n("GridColumn",{attrs:{field:"ck",width:"30",align:"center"},scopedSlots:e._u([{key:"header",fn:function(t){return[n("CheckBox",{on:{checkedChange:function(t){return e.onAllCheckedChange(t)}},model:{value:e.allChecked,callback:function(t){e.allChecked=t},expression:"allChecked"}})]}},{key:"body",fn:function(t){return[n("CheckBox",{on:{checkedChange:function(t){return e.onCheckedChange(t)}},model:{value:t.row.selected,callback:function(n){e.$set(t.row,"selected",n)},expression:"scope.row.selected"}})]}}])}),e._v(" "),n("GridColumn",{attrs:{cellCss:"datagrid-td-rownumber",width:"50",align:"center"},scopedSlots:e._u([{key:"body",fn:function(t){return[e._v("\n                        "+e._s(t.rowIndex+1)+"\n                    ")]}}])},[n("template",{slot:"header"},[e._v("\n                        No.\n                    ")])],2),e._v(" "),n("GridColumn",{attrs:{field:"scenarioName",title:"시나리오명",halign:"center"}}),e._v(" "),n("GridColumn",{attrs:{field:"loopCount",title:"반복 횟수",width:"60",align:"center"}}),e._v(" "),n("GridColumn",{attrs:{title:"등록된 동작 개수",align:"center"},scopedSlots:e._u([{key:"body",fn:function(t){return[e._v("\n                        "+e._s(t.row.actionList.length)+"\n                    ")]}}])})],1)],2),e._v(" "),n("LayoutPanel",{staticStyle:{width:"100%",height:"100%"},attrs:{region:"center",title:"시나리오 내용"}},[n("el-form",{ref:"scenarioForm",attrs:{model:e.scenarioForm,rules:e.rules,"label-position":"left","label-width":"100px",size:"mini"}},[n("el-form-item",{attrs:{label:"시나리오명",prop:"scenarioName"}},[n("el-input",{attrs:{placeholder:"예약모듈 시나리오"},model:{value:e.scenarioForm.scenarioName,callback:function(t){e.$set(e.scenarioForm,"scenarioName",t)},expression:"scenarioForm.scenarioName"}})],1),e._v(" "),n("el-form-item",{attrs:{label:"반복 횟수",prop:"loopCount"}},[n("el-input-number",{attrs:{min:1},model:{value:e.scenarioForm.loopCount,callback:function(t){e.$set(e.scenarioForm,"loopCount",t)},expression:"scenarioForm.loopCount"}})],1),e._v(" "),n("el-transfer",{attrs:{data:e.unRegisterAction,titles:["미등록 동작","등록된 동작"],"filter-placeholder":"검색어 입력",filterable:""},model:{value:e.registerAction,callback:function(t){e.registerAction=t},expression:"registerAction"}}),e._v(" "),n("el-form-item",[n("el-button",{attrs:{type:"info"},on:{click:function(t){return e.saveScenario("scenarioForm")}}},[e._v("저장")])],1)],1)],1)],1),e._v(" "),n("el-dialog",{attrs:{title:"삭제된 시나리오 목록",visible:e.dialogDeletedScenario,width:"800px"},on:{"update:visible":function(t){e.dialogDeletedScenario=t}}},[0==e.deletedScenarioData.length?n("span",[e._v("삭제된 시나리오가 없습니다")]):e._e(),e._v(" "),0!=e.deletedScenarioData.length?n("DataGrid",{staticStyle:{height:"400px"},attrs:{data:e.deletedScenarioData,loading:e.deletedLoading}},[n("GridColumn",{attrs:{align:"center",cellCss:"datagrid-td-rownumber",width:"30"},scopedSlots:e._u([{key:"body",fn:function(t){return[e._v("\n                    "+e._s(t.rowIndex+1)+"\n                ")]}}],null,!1,3393989708)},[n("template",{slot:"header"},[e._v("\n                    No.\n                ")])],2),e._v(" "),n("GridColumn",{attrs:{field:"scenarioName",title:"시나리오명",halign:"center"}}),e._v(" "),n("GridColumn",{attrs:{field:"loopCount",title:"반복 횟수",align:"center"}}),e._v(" "),n("GridColumn",{attrs:{field:"registerActionCount",title:"등록된 동작 개수",align:"center"}}),e._v(" "),n("GridColumn",{attrs:{field:"delDt",title:"삭제일시",align:"center"}}),e._v(" "),n("GridColumn",{attrs:{title:"",width:"50",align:"center"},scopedSlots:e._u([{key:"body",fn:function(t){return[n("LinkButton",{on:{click:function(n){return e.recoveryScenario(t.row)}}},[e._v("복원")])]}}],null,!1,3501991017)})],1):e._e()],1)],1)},staticRenderFns:[]};var d=n("VU/8")(s,l,!1,function(e){n("ymTT"),n("QC2H")},"data-v-8907ffc6",null);t.default=d.exports},ymTT:function(e,t){}});
//# sourceMappingURL=1.e0d6284767c925f11cbd.js.map