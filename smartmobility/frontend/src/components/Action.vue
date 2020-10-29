<template>
    <div>
        <Layout style="width: 100%; height: 800px">
            <LayoutPanel region="north" :border="false" style="height: 45px; text-align: right;">
                <Buttons :buttonEvents="buttonEvents"/>
            </LayoutPanel>

            <LayoutPanel region="west" title="동작목록" style="width: 650px;">
                <template slot="header">
                    <div class="f-row">
                        <div class="panel-title f-full">동작목록</div>
                        <LinkButton @click="showDeletedAction()">삭제된 동작</LinkButton>
                    </div>
                </template>

                <DataGrid
                    ref="actionList"
                    :data="data"
                    :columnResizing="true"
                    :border="false"
                    :loading="loading"
                    selectionMode="single"
                    @selectionChange="actionClick($event)"
                    style="width: 100%; height: 100%;">
                    <GridColumn field="ck" width="30" align="center">
                        <template slot="header" slot-scope="scope">
                            <CheckBox v-model="allChecked" @checkedChange="onAllCheckedChange($event)"></CheckBox>
                        </template>
                        <template slot="body" slot-scope="scope">
                            <CheckBox v-model="scope.row.selected" @checkedChange="onCheckedChange($event)"></CheckBox>
                        </template>
                    </GridColumn>
                    <GridColumn align="center" cellCss="datagrid-td-rownumber" width="50">
                        <template slot="header">
                            No.
                        </template>
                        <template slot="body" slot-scope="scope">
                            {{ scope.rowIndex + 1 }}
                        </template>
                    </GridColumn>
                    <GridColumn field="actionName" title="동작명" width="80" halign="center"></GridColumn>
                    <GridColumn field="userCount" title="유저수" width="60" align="center"></GridColumn>
                    <GridColumn field="timeSet" title="실행 시간" halign="center"></GridColumn>
                    <GridColumn field="loopCount" title="반복 횟수" width="60" align="center"></GridColumn>
                    
                    
                    
                    <GridColumn field="type" title="Type" width="60" align="center"></GridColumn>
                    <GridColumn field="url" title="URL" halign="center"></GridColumn>
                    <GridColumn field="parameter" title="Parameter" halign="center"></GridColumn>
                </DataGrid>
            </LayoutPanel>

            <LayoutPanel region="center" title="동작내용" style="width: 100%; height: 100%;">
                <el-form ref="actionForm" :model="actionForm" :rules="rules" label-position="left" label-width="100px" size="mini">
                    <el-form-item label="동작명" prop="actionName">
                        <el-input v-model="actionForm.actionName" placeholder="예약모듈"></el-input>
                    </el-form-item>

                    <el-form-item label="유저수" prop="userCount">
                        <el-input-number v-model="actionForm.userCount" :min="1"></el-input-number>
                    </el-form-item>

                    <el-form-item label="실행 시간" prop="timeSet">
                        <el-input v-model="actionForm.timeSet"></el-input>
                    </el-form-item>

                    <el-form-item label="반복 횟수" prop="loopCount">
                        <el-input-number v-model="actionForm.loopCount" :min="1"></el-input-number>
                    </el-form-item>



                    <el-form-item label="URL" prop="url">
                        <el-input v-model="actionForm.url" placeholder="http://localhost:8080/chargeToken">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="Type" prop="type">
                        <el-select v-model="actionForm.type">
                            <el-option label="GET" value="GET"></el-option>
                            <el-option label="POST" value="POST"></el-option>
                        </el-select>
                    </el-form-item>

                    <el-form-item label="Encoding" prop="encoding">
                        <el-select v-model="actionForm.encoding">
                            <el-option label="EUC-KR" value="EUC-KR"></el-option>
                            <el-option label="UTF-8" value="UTF-8"></el-option>
                        </el-select>
                    </el-form-item>

                    <el-form-item label="Timeout" prop="timeout">
                        <el-input v-model="actionForm.timeout"></el-input>
                    </el-form-item>

                    <el-form-item label="Parameter" prop="parameter">
                        <el-input type="textarea" v-model="actionForm.parameter" placeholder="param1=a&param2=b&param3=c..."></el-input>
                    </el-form-item>

                    <el-form-item>
                        <el-button type="info" @click="saveAction('actionForm')">저장</el-button>
                    </el-form-item>

               
                    


                </el-form>
            </LayoutPanel>
        </Layout>

        <el-dialog title="삭제된 동작 목록" :visible.sync="dialogDeletedAction" width="900px">
            <span v-if="deletedActionData.length == 0">삭제된 동작이 없습니다</span>
            <DataGrid
                v-if="deletedActionData.length != 0"
                :data="deletedActionData"
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
                <GridColumn field="actionName" title="동작명" width="100" halign="center"></GridColumn>
                <GridColumn field="userCount" title="유저수" width="60" align="center"></GridColumn>
                <GridColumn field="loopCount" title="반복 횟수" width="60" align="center"></GridColumn>
                <GridColumn field="type" title="Type" width="60" align="center"></GridCOlumn>
                <GridColumn field="url" title="URL" halign="center"></GridColumn>
                <GridColumn field="parameter" title="Parameter" halign="center"></GridColumn>
                <GridColumn field="delDt" title="삭제일시" align="center"></GridColumn>
                <GridColumn title="" width="50" align="center">
                    <template slot="body" slot-scope="scope">
                        <LinkButton @click="recoveryAction(scope.row)">복원</LinkButton>
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
            buttonEvents: {
                refresh: {
                    show: true,
                    event: this.loadGrid
                },
                add: {
                    show: true,
                    event: this.addAction
                },
                delete: {
                    show: true,
                    event: this.deleteAction
                },
                execute: {
                    show: true,
                    event: this.executeAction
                },
                pause: {
                    show: true,
                    event: this.pauseAction
                }
            },
            actionForm: {
                actionId: '',
                actionName: '',
                userCount: 1,
                loopCount: 1,
                timeSet : 1,
                url: '',
                type: 'GET',
                encoding: 'UTF-8',
                timeout: 3000,
                parameter: ''
            },
            rules: {
                actionName: [
                    { required: true, message: '동작명을 입력해주세요', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            var regex = /[-]/gi
                            !value.match(regex) ? callback() : callback(new Error('동작명에 \'-\' 문자를 포함할 수 없습니다'))
                        }
                    }
                ],
                userCount: [
                    { required: true, message: '유저수를 입력해주세요', trigger: 'blur' }
                ],
                loopCount: [
                    { required: true, message: '반복횟수를 입력해주세요', trigger: 'blur' }
                ],
                timeSet: [
                    { required: true, message: '실행시간을 입력해주세요', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            var regex = /^[0-9]+$/
                            value.match(regex) ? callback() : callback(new Error('실행 시간은 숫자만 입력 가능합니다.'))
                        }
                    }
                ],
                url: [
                    { required: true, message: 'URL을 입력해주세요', trigger: 'blur' },
                    {   // URL valication 정규식 처리
                        validator: (rule, value, callback) => {
                            var regex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/
                            value.match(regex) ? callback() : callback(new Error('올바른 URL을 입력해주세요'))
                        }
                    }
                ],
                type: [
                    { required: true, message: 'Type을 선택해주세요', trigger: 'blur' }
                ],
                encoding: [
                    { required: true, message: 'Encoding을 선택해주세요', trigger: 'blur' }
                ],
                timeout: [
                    { required: true, message: 'Timeout을 입력해주세요', trigger: 'blur' }
                ],
                /*
                parameter: [
                    {   // 파라미터 validation 정규식 처리
                        validator: (rule, value, callback) => {
                            var regex = /^\?([\w-]+(=[\w-]*)?(&[\w-]+(=[\w-]*)?)*)?$/

                            if(regex.test('?' + value))
                                callback()
                            else
                                callback(new Error('Parameter형식에 맞게 입력해주세요'))
                        }
                    }
                ]
                //*/
            },
            data: [],
            deletedActionData: [],
            allChecked: false,
            rowClicked: false,
            isUpdate: true,
            loading: false,
            focusIndex: 0,
            dialogDeletedAction: false,
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

        // 동작목록 load / reload
        loadGrid(isAddAndDelete) {
            this.loading = true
            this.axios.get('/api/v1/action').then(response => {
                this.loading = false
                response.data.actionList.forEach((row, index) => {
                    row['index'] = index
                })
                this.data = response.data.actionList

                if(this.data.length == 0) {
                    this.isUpdate = false
                    this.allChecked = false
                    this.resetActionForm()
                } else {
                    var index

                    // 동작 삭제후 목록 로딩시 리스트의 가장 아래부분 동작 선택(포커스)
                    // 아닐 경우(동작 수정) 저장된 포커스 인덱스 동작 선택
                    if(isAddAndDelete)
                        index = this.data.length - 1
                    else 
                        index = this.focusIndex

                    this.$refs.actionList.selectRow(this.data[index])
                }
            }).catch(ex => {
                this.loading = false
                console.log(ex)
            })
        },

        // 삭제된 동작목록 load / reload
        loadDeletedGrid() {
            this.axios.get('/api/v1/action/deleted').then(response => {
                this.deletedActionData = response.data.deletedActionList
                this.dialogDeletedAction = true
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

        // 동작내용 폼 초기화
        resetActionForm() {
            this.$refs['actionForm'].resetFields()
        },

        // 동작목록에서 동작 클릭시 이벤트
        actionClick(row) {
            this.isUpdate = true
            if(row) {
                this.focusIndex = row.index
                var copy = Object.assign({}, row)
                delete copy.selected
                this.actionForm = copy
            }
        },

        // 동작내용 폼에서 저장버튼 클릭시 이벤트
        saveAction(form) {
            this.$refs[form].validate((valid) => {
                if(valid) {
                    // 수정인지 추가인지 여부
                    // 수정
                    if(this.isUpdate) {
                        this.axios.put('/api/v1/action', this.actionForm).then(response => {
                            this.loadGrid()
                        })
                    }
                    // 추가
                    else {
                        this.axios.post('/api/v1/action', this.actionForm).then(response => {
                            this.loadGrid(true)
                        })
                    }
                    this.notification('saveAction')
                } else {
                    return false
                }
            })
        },

        // 동작에서 추가 버튼 클릭시
        addAction() {
            this.resetActionForm()
            this.$refs.actionList.clearSelections()
            this.$nextTick(() => this.isUpdate = false)
        },

        // 동작에서 삭제 버튼 클릭시
        deleteAction() {
            if(this.checkedRows.length == 0) {
                this.notification('deleteActionCheck')
            } else {
                this.$confirm('체크한 동작들을 삭제하시겠습니까?', {
                    confirmButtonText: '삭제',
                    cancelButtonText: '취소',
                    type: 'warning'
                }).then(() => {
                    // 체크된 동작중 Id 값만 추출
                    var actionIds = []
                    this.checkedRows.forEach(row => actionIds.push(row.actionId))

                    this.axios.delete('/api/v1/action', {
                        data: {
                            actionIds: actionIds
                        }
                    }).then(response => {
                        if(response.data) {
                            this.loadGrid(true)
                            this.notification('deleteAction')
                        }
                    }).catch(ex => {
                        this.notification('deleteActionError')
                    })
                })
            }
        },

        // 동작에서 실행 버튼 클릭시 
        executeAction() {
            if(this.checkedRows.length == 0) {
                this.notification('executeActionCheck')
            } else {
                this.setIsExecute({
                    isExecute: true,
                    message: '동작이 실행중입니다'
                })
                this.axios.post('/api/v1/action/execute', {
                    actionList: this.checkedRows
                })
            }
        },

        // 동작에서 중지 버튼 클릭시
        pauseAction() {
            this.axios.post('/api/v1/action/pause').then(response => {
                this.setIsExecute({ isExecute: false })
            }).catch(ex => {
                this.setIsExecute({ isExecute: false })
                console.log(ex)
            })
        },
        
        // 삭제된 동작 버튼 클릭시
        showDeletedAction() {
            this.loadDeletedGrid()
        },

        // 동작 복원 버튼 클릭시
        recoveryAction(row) {
            this.$confirm('동작을 복원하시겠습니까?', '동작복원', {
                confirmButtonText: '복원',
                cancelButtonText: '취소',
                type: 'info'
            }).then(() => {
                this.axios.post('/api/v1/action/recovery', {
                    actionIds: [row.actionId]
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

.checkbox {
    width: 17px;
    height: 17px;
}

.l-btn {
    height: 20px;
}
</style>

<style>
.l-btn-text {
    font-size: 12px;
}
</style>
