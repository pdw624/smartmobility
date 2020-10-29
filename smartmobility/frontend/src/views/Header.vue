<template>
    <div>
        <el-row>
            <el-col :span="12">
                <header class="title">
                    <img src="../assets/images/logo.png" style="margin-left: 10px; margin-top: 10px;">
                </header>
            </el-col>
            <el-col :span="12">
                <el-menu :router="true" :default-active="activeIndex" mode="horizontal">
                    <el-menu-item @click="openTokenForm()">
                        API키 설정
                    </el-menu-item>

                    <el-menu-item index="/statistics">
                        통계조회
                    </el-menu-item>

                    <el-menu-item index="/reservation">
                        예약관리
                    </el-menu-item>
                    
                    <el-menu-item index="/scenario">
                        시나리오설정
                    </el-menu-item>

                    <el-menu-item index="/">
                        동작설정
                    </el-menu-item>
                    <!--테스트-->
                     <el-menu-item index="/totalInfo">
                        종합집계
                    </el-menu-item>


                     <el-menu-item index="/chart">
                        통계 그래프
                    </el-menu-item>

                </el-menu>
            </el-col>
        </el-row>

        <el-dialog title="API키 설정" :visible.sync="isVisible" width="500px" height="200px">
            <el-form :model="form" ref="form" :rules="rules" label-position="left" label-width="70px" size="mini">
                <el-form-item label="apiKey" prop="token">
                    <el-input v-model="form.token"></el-input>
                </el-form-item>
                <el-form-item style="text-align: right;">
                    <el-button type="info" @click="saveToken('form')">저장</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            activeIndex: window.location.pathname,
            isVisible: false,
            form: {
                token: ''
            },
            rules: {
                token: [
                    { required: true, message: 'API 키를 입력해주세요', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            var regex = /^[A-Za-z0-9+]*$/

                            if(regex.test(value))
                                callback()
                            else
                                callback(new Error('올바른 API 키 값을 입력해주세요(영문 + 숫자)'))
                        }
                    }
                ]
            },
        }
    },

    mounted() {
        this.init()
    },

    methods: {
        init() {
            axios.get('/api/v1/token').then(response => {
                this.form.token = response.data
            })
        },
        openTokenForm() {
            this.isVisible = true
        },
        saveToken(form) {
            this.$refs[form].validate(valid => {
                if(valid) {
                    axios.put('/api/v1/token', { value: this.form.token }).then(response => {
                        if(response.data) {
                            this.$notify({
                                title: 'API Key 저장',
                                message: 'API Key 저장에 성공하였습니다',
                                type: 'success',
                                duration: 1500,
                                offset: 80
                            })
                        }
                    })
                }
            })
        }
    }
}
</script>

<style scoped>
.el-menu.el-menu--horizontal {
    border-width: 0;
}

.el-menu--horizontal > .el-menu-item {
    float: right;
    height: 50px;
    line-height: 50px;
}

.title {
    height: 50px;
    line-height: 50px;
    vertical-align: middle;
    font-weight: 900;
}
</style>