<template>
    <div>
        <Layout style="width: 100%; height: 800px">
            <LayoutPanel region="north" :border="false" style="height: 45px; text-align: right;">
                <Buttons :buttonEvents="buttonEvents"/>
            </LayoutPanel>
            
            <LayoutPanel region="west" title="시나리오 목록" style="width: 450px;">
                <template slot="header">
                    <div class="f-row">
                        <div class="panel-title f-full">예약 시나리오 목록</div>
                        <LinkButton @click="showDeletedScenario()">삭제된 시나리오</LinkButton>
                    </div>
                </template>

                <DataGrid
                    ref="scenarioList"
                    :data="data"
                    :columnResizing="true"
                    :border="false"
                    :loading="loading"
                    selectionMode="single"
                    @selectionChange="scenarioClick($event)"
                    style="width: 100%; height: 100%;">

                    <GridColumn field="ck" width="30" align="center">
                        <template slot="header" slot-scope="scope">
                            <CheckBox v-model="allChecked" @checkedChange="onAllCheckedChange($event)"></CheckBox>
                        </template>
                        <template slot="body" slot-scope="scope">
                            <CheckBox v-model="scope.row.selected" @checkedChange="onCheckedChange($event)"></CheckBox>
                        </template>
                    </GridColumn>
                    
                    <GridColumn cellCss="datagrid-td-rownumber" width="50" align="center">
                        <template slot="header">
                            No.
                        </template>
                        <template slot="body" slot-scope="scope">
                            {{ scope.rowIndex + 1 }}
                        </template>
                    </GridColumn>
                    <GridColumn field="scenarioName" title="시나리오명" halign="60"></GridColumn>
                    <GridColumn field="reserveTime" title="예약 날짜" width="center" align="center"></GridColumn>
                    <GridColumn field="status" title="실행 유무" width="60" align="center"></GridColumn>
                </DataGrid>
            </LayoutPanel>

            <LayoutPanel region="center" title="시나리오 내용" style="width: 100%; height: 100%;">
                <el-form ref="scenarioForm" :model="scenarioForm" :rules="rules" label-position="left" label-width="100px" size="mini">
                    <el-form-item label="시나리오명" prop="scenarioName">
                        <el-input v-model="scenarioForm.scenarioName" placeholder="예약모듈 시나리오" :disabled='true'></el-input>
                    </el-form-item>

                    <!--el-form-item label="반복 횟수" prop="loopCount">
                        <el-input-number v-model="scenarioForm.loopCount" :min="1" :disabled='true'></el-input-number>
                    </el-form-item-->



                    <!--
                    <el-transfer
                        :data="unRegisterAction"
                        v-model="registerAction"
                        :titles="['미등록 동작', '등록된 동작']"
                        filter-placeholder="검색어 입력"
                        filterable>
                    </el-transfer>
                    -->

                <!--     
                    <template>
                        <el-form-item label="예약날짜" prop="reserveTime">
                            <div class="block">
                                <span class="demonstration">With shortcuts</span>
                                <el-date-picker
                                v-model="scenarioForm.reserveTime"
                                type="datetime"
                                placeholder="Select date and time"
                                :picker-options="pickerOptions">
                                </el-date-picker>
                            </div>
                        </el-form-item>
                    </template>
                -->


                   
                    <el-form-item label="예약날짜" prop="reserveTime">
                        <el-input v-model="scenarioForm.reserveTime" placeholder="20201212_14:30" :disabled='true'></el-input>
                    </el-form-item>
                   
                    
                    <!--el-form-item>
                        <el-button type="info" @click="saveScenario('scenarioForm')" :disabled='true'>예약</el-button>
                    </el-form-item-->
                    
                    <el-form-item>
                        <el-button type="info" id="isCancel" @click="cancelReserve()">예약 삭제</el-button>
                    </el-form-item>
                    
                </el-form>
            </LayoutPanel>
        </Layout>

        <el-dialog title="삭제된 시나리오 목록" :visible.sync="dialogDeletedScenario" width="800px">
            <span v-if="deletedScenarioData.length == 0">삭제된 시나리오가 없습니다</span>
            <DataGrid
                v-if="deletedScenarioData.length != 0"
                :data="deletedScenarioData"
                :loading="deletedLoading"
                style="height: 400px;">
                <GridColumn align="center" cellCss="datagrid-td-rownumber" width="30">
                    <template slot="header">
                        No.
                    </template>
                    <template slot="body" slot-scope="scope">
                        {{ scope.rowIndex + 1 }}
                    </template>
                </GridColumn>
                <GridColumn field="scenarioName" title="시나리오명" halign="center"></GridColumn>
                <GridColumn field="loopCount" title="반복 횟수" align="center"></GridColumn>
                <GridColumn field="registerActionCount" title="등록된 동작 개수" align="center"></GridColumn>
                <GridColumn field="delDt" title="삭제일시" align="center"></GridColumn>
                <GridColumn title="" width="50" align="center">
                    <template slot="body" slot-scope="scope">
                        <LinkButton @click="recoveryScenario(scope.row)">복원</LinkButton>
                    </template>
                </GridColumn>
            </DataGrid>
        </el-dialog>
    </div>
</template>

<script>
import { mapMutations } from 'vuex'
import Buttons from './Buttons'

export default {
    components: {
        Buttons
    },
    data() {
        return {

            pickerOptions: {
                shortcuts: [{
                text: 'Today',
                onClick(picker) {
                    picker.$emit('pick', new Date());
                }
                }, {
                text: 'Tomorrow',
                onClick(picker) {
                    const date = new Date();
                    date.setTime(date.getTime() + 3600 * 1000 * 24);
                    picker.$emit('pick', date);
                }
                }, {
                text: 'A week ago',
                onClick(picker) {
                    const date = new Date();
                    date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
                    picker.$emit('pick', date);
                }
                }]
            },




            buttonEvents: {

                refresh: {
                    show: true,
                    event: this.loadGrid
                },
                pause: {
                    show: true,
                    event: this.pauseScenario
                }
            },
            scenarioForm: {
                scenarioName: '',
                userCount: 1,
                loopCount: 1
            },
            unRegisterAction: [],
            registerAction: [],
            
            rules: {
                scenarioName: [
                    { required: true, message: '시나리오명을 입력해주세요', trigger: 'blur' }
                ],
                userCount: [
                    { required: true, message: '유저수를 입력해주세요', trigger: 'blur' }
                ],
                loopCount: [
                    { required: true, message: '반복횟수를 입력해주세요', trigger: 'blur' }
                ],
                reserveTime: [
                    { required: true, message: '예약날짜를 입력해주세요', trigger: 'blur' }
                ]
            },
            reserveTime : '',
            data: [],
            deletedScenarioData: [],
            allChecked: false,
            rowClicked: false,
            isUpdate: true,
            loading: false,
            focusIndex: 0,
            dialogDeletedScenario: false,
            deletedLoading: false
        }
    },

    mounted() {
        this.loadGrid()
    },

    computed: {
        checkedRows() {
            return this.data.filter(row => row.selected)
        }
    },

    methods: {
        ...mapMutations([
            'setIsExecute'
        ]),

        //예약 삭제 버튼 클릭 시
        cancelReserve(form) {
            if(this.checkedRows.length == 0) {
                this.notification('deleteScenarioCheck')
            } else {
                this.$confirm('체크한 예약시나리오들을 삭제하시겠습니까? (삭제된 예약은 실행되지 않습니다.)', {
                    confirmButtonText: '삭제',
                    cancelButtonText: '취소',
                    type: 'warning'
                }).then(() => {
                    var reserveIds = []
                    this.checkedRows.forEach(row => reserveIds.push(row.reserveId))

                    this.axios.delete('/api/v1/scenario/reserve', {
                        data: {
                            reserveIds: reserveIds
                        }
                    }).then(response => {
                        if(response.data) {
                            this.loadGrid(true)
                            this.notification('deleteScenario')
                        }
                    }).catch(ex => {
                        this.notification('deleteScenarioError')
                    })
                })
            }
        },
        
        timer() {
            setTimeout(frontReserve, 1000);
        },


        // 시나리오 목록 load / reload
        loadGrid(isAddAndDelete) {
            this.loading = true
            this.axios.get('/api/v1/scenario/reserve').then(response => {
                this.loading = false
                
                response.data.reserveList.forEach((row, index) => {
                    row['index'] = index
                })
                
                this.data = response.data.reserveList
                
                this.unRegisterAction = response.data.actionList

                if(this.data.length == 0) {
                    this.isUpdate = false
                    this.allChecked = false
                    this.resetScenarioForm()
                } else {
                    var index

                    if(isAddAndDelete)
                        index = this.data.length - 1
                    else
                        index = this.focusIndex

                    this.$refs.scenarioList.selectRow(this.data[index])
                }
            }).catch(ex => {
                this.loading = false
                console.log(ex)
            })
        },

        // 삭제된 시나리오목록 load / reload
        loadDeletedGrid() {
            this.axios.get('/api/v1/scenario/deleted').then(response => {
                this.deletedScenarioData = response.data.deletedScenarioList
                this.dialogDeletedScenario = true
            }).catch(ex => {
                console.log(ex)
            })
        },

        // 전체 체크시 발생 이벤트
        onAllCheckedChange(checked) {
            if(this.rowClicked) {
                return
            }
            this.data = this.data.map(row => {
                return Object.assign({}, row, {
                    selected: checked
                })
            })
        },

        // 한 개의 항목 체크시 발생 이벤트
        onCheckedChange(checked) {
            this.allChecked = this.checkedRows.length === this.data.length
            this.rowClicked = true
            this.$nextTick(() => (this.rowClicked = false))
        },

        // 시나리오 폼 초기화
        resetScenarioForm() {
            this.$refs['scenarioForm'].resetFields()
            this.registerAction = []
        },

        // 시나리오목록에서 시나리오 클릭시 이벤트
        scenarioClick(row) {
            this.isUpdate = true

            if(row) {
                this.focusIndex = row.index
                var copy = Object.assign({}, row)

                this.scenarioForm = copy

                var actionIds = []
                
                for(var i = 0; i < row.actionList.length; i++)
                    actionIds.push(row.actionList[i].actionId)
                    
                
                console.log("actID:"+actionIds)
                
                this.registerAction = actionIds
                //this.registerAction.push({disabled: })
                
                //park
                //this.registerAction.push({disabled: row.actionList[i].actionId})
                //this.unRegisterAction.push({disabled: row.actionList[i].actionId})
                //
            }
        },

        // 시나리오 폼에서 저장버튼 클릭시 이벤트
        saveScenario(form) {
            this.$refs[form].validate(valid => {
                if(valid) {
                    if(this.registerAction.length == 0) {
                        this.notification('saveScenarioCheck')
                        return false
                    }

                    var params = {
                        scenario: {
                            scenarioName: this.scenarioForm.scenarioName,
                            loopCount: this.scenarioForm.loopCount,
                            userCount: this.scenarioForm.userCount,
                            reserveTime : this.scenarioForm.reserveTime,
                            scenarioList: this.checkedRows
                        },
                        actionIds: this.registerAction
                    }
                    //asdsad
                     if(this.checkedRows.length == 0) {
                        this.notification('executeScenarioCheck')
                    }else{
                        if(this.isUpdate) {
                        params.scenario['scenarioId'] = this.scenarioForm.scenarioId
                            this.axios.post('/api/v1/scenario/reserve', params).then(response => {
                                this.loadGrid()
                                this.notification('saveScenario')
                            })
                        } else {
                            this.axios.post('/api/v1/scenario/reserve', params).then(response => {
                                this.loadGrid(true)
                                this.notification('saveScenario')
                            })
                        }
                    }
                    
                } else {
                    return false
                }
            })
        },

        // 시나리오 추가 버튼 클릭시
        addScenario() {
            this.resetScenarioForm()
            this.$refs.scenarioList.clearSelections()
            this.$nextTick(() => this.isUpdate = false)
        },

        // 시나리오 삭제 버튼 클릭시
        deleteScenario() {
            if(this.checkedRows.length == 0) {
                this.notification('deleteScenarioCheck')
            } else {
                this.$confirm('체크한 시나리오들을 삭제하시겠습니까?', {
                    confirmButtonText: '삭제',
                    cancelButtonText: '취소',
                    type: 'warning'
                }).then(() => {
                    var scenarioIds = []
                    this.checkedRows.forEach(row => scenarioIds.push(row.scenarioId))

                    this.axios.delete('/api/v1/scenario', {
                        data: {
                            scenarioIds: scenarioIds
                        }
                    }).then(response => {
                        if(response.data) {
                            this.loadGrid(true)
                            this.notification('deleteScenario')
                        }
                    }).catch(ex => {
                        this.notification('deleteScenarioError')
                    })
                })
            }
        },

        // 시나리오 실행 버튼 클릭시
        executeScenario() {
            if(this.checkedRows.length == 0) {
                this.notification('executeScenarioCheck')
            } else {
                this.setIsExecute({
                    isExecute: true,
                    message: '시나리오가 실행중입니다'
                })
                this.axios.post('/api/v1/scenario/execute', {
                    scenarioList: this.checkedRows
                }).then(response => {
                })
            }
        },

        // 시나리오 중지 버튼 클릭시
        pauseScenario() {
            this.axios.post('/api/v1/scenario/pause').then(response => {
                this.setIsExecute({ isExecute: false })
            }).catch(ex => {
                this.setIsExecute({ isExecute: false })
                console.log(ex)
            })
        },

        // 삭제된 시나리오 버튼 클릭시
        showDeletedScenario() {
            this.loadDeletedGrid()
        },

        // 시나리오 복원 버튼 클릭시
        recoveryScenario(row) {
            this.$confirm('시나리오를 복원하시겠습니까?', '시나리오복원', {
                confirmButtonText: '복원',
                cancelButtonText: '취소',
                type: 'info'
            }).then(() => {
                this.axios.post('/api/v1/scenario/recovery', {
                    scenarioIds: [row.scenarioId]
                }).then(response => {
                    this.loadGrid()
                    this.loadDeletedGrid()
                }).catch(() => {

                })
            })
        },
    }
}
</script>

<style scoped>
.el-form {
    margin-top: 20px;
    margin-left: 20px;
}

.el-input, .el-textarea {
    width: 600px;
}

.el-divider {
    margin: 9px 0;
}

.checkbox {
    width: 17px;
    height: 17px;
}

.l-btn {
    height: 20px;
}

.el-transfer {
    margin-left: 100px;
    margin-bottom: 15px;
}
</style>

<style>
.l-btn-text {
    font-size: 12px;
}
</style>