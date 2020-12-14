<template>
    <div>
        <Layout style="width: 100%; height: 800px">
            <el-button @click="createQuery()">
                query
            </el-button>
            
        </Layout>

        
    </div>
</template>

<script>
import { mapMutations } from 'vuex'
import Buttons from './Buttons'

export default {
    components: {
        //Buttons
    },
    data() {
        return {
            
        }
    },

    mounted() {
    },

    computed: {
    },

    methods: {
        ...mapMutations([
            
        ]),
        createQuery(row) {
            alert("hello")
            this.axios.post('/api/v1/scenario/query', {
                
            }).then(response => {
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