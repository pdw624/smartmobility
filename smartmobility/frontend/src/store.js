import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        isExecute: false,
        executeMessage: '',
        //park
        // isReserve: false,
        // reserveMessage: ''
    },

    getters: {
        getIsExecute: state => state.isExecute,
        getExecuteMessage: state => state.executeMessage,
        //park
        // getIsReserve: state => state.isReserve,
        // getReserveMessage: state => state.reserveMessage,
    },

    mutations: {
        setIsExecute: (state, payload) => {
            state.isExecute = payload.isExecute
            
            if(payload.message != null && typeof payload.message != 'undefined')
                state.executeMessage = payload.message
                //park
                var len = state.executeMessage.length
                console.log("페이로드메시지 : "+payload.message)
                console.log("페이로드길이 : "+len)
                
                //state.executeMessage = '예약 대기중입니다.'
            
        },
        //park
        // setIsReserve: (state, payload) => {
        //     state.isReserve = payload.isReserve

        //     if(payload.message != null && typeof payload.message != 'undefined')
        //         state.reserveMessage = payload.message
                
        // }
    },

    actions: {
    }
})