<template>
	<div id="app">
		<Header />
		<Body />
		<Footer />
	</div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'

import Header from './views/Header'
import Body from './views/Body'
import Footer from './views/Footer'

import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'

export default {
	name: 'App',
	components: {
		Header,
		Body,
		Footer
	},

	data() {
		return {
			connected: false,
			message: {
				AT: () => {
					this.notification('executeAction')
					this.setIsExecute({ isExecute: false })
				},
				SR: () => {
					this.notification('executeScenario')
					this.setIsExecute({ isExecute: false })
				},
				PS: () => {
					this.notification('pause')
					this.setIsExecute({ isExecute: false })
				},
				TC: (totalCount) => {
					this.totalCount = totalCount
				},
				CT: (count) => {
					this.messageObj.message = this.getExecuteMessage + ' (' + count + '/' + this.totalCount + ')'
				}
			},
			recInterval: null,
			messageObj: null,
			totalCount: 0
		}
	},

	mounted() {
		this.connect()
		//this.reserveScenarioLoad()
	},

	computed: {
		...mapGetters([
			'getIsExecute',
			'getExecuteMessage',
			//park
			// 'getIsReserve',
			// 'getReserveMessage'
		])
	},

	watch: {
		getIsExecute(value) {
			if(value) {
				this.messageObj = this.$message({
					message: this.getExecuteMessage,
					center: true,
					duration: 0,
					iconClass: 'el-icon-loading',
					customClass: 'message-box'
				})
			} else {
				this.messageObj.close()
			}
		},
		//park
		// getIsReserve(value) {
		// 	if(value) {
		// 		this.messageObj = this.$message({
		// 			message: this.getReserveMessage,
		// 			center: true,
		// 			duration: 0,
		// 			iconClass: 'el-icon-loading',
		// 			customClass: 'message-box'
		// 		})
		// 	} else {
		// 		this.messageObj.close()
		// 	}
		// }
	},

	methods: {
		...mapMutations([
			'setIsExecute'
			//park
			//,'setIsReserve'
		]),

		connect() {
			this.socket = new SockJS('/socket')
			this.stompClient = Stomp.over(this.socket)
			this.stompClient.debug = () => {}
			this.stompClient.connect(
				{},
				frame => {
					clearInterval(this.recInterval)
					this.connected = true
					this.stompClient.subscribe('/message', tick => {
						var result = JSON.parse(tick.body)
						this.message[result.type](result.payload)
					})
				},
				message => {
					console.log('WebSocket Disconnect!')
					clearInterval(this.recInterval)
					this.recInterval = setInterval(() => {
						console.log('WebSocket Reconnect...')
						this.connect()
					}, 2000)
				}
			)

			
		},

		disconnect() {
			if(this.stompClient)
				this.stompClient.disconnect()
			this.connected = false
		},

		reserveScenarioLoad() {
            // console.log("asdfasdf", JSON.parse(localStorage.getItem('reserveList')));
			// var pparam = JSON.parse(localStorage.getItem('reserveList'));

			this.axios.post('/api/v1/scenario/startup').then(response => {
				console.log("최종1",response.data)

				if (response.data.overlaps != null){
					// 메시지 띄우는 로직
					//alert("중복임...");
					this.notification('overlapReserveScenario')
					return;
				}
				this.loadGrid()
				this.notification('saveScenario')
				//park 추가
				this.setIsExecute({
					//isExecute: true,
					//message: '동작이 실행중입니다'
				})
				
				// this.setIsReserve({
				//     isReserve: true,
				//     message: '예약 대기중입니다.'
				// })
				
			})
			// //localStorage.removeItem("reserveList");
        },
	}
}
</script>

<style>
html {
	height: 100%;
}

body {
	height :100%;
}

@font-face {
	font-family: 'NanumBarunGothic';
	src: url('./assets/font/NanumBarunGothic.eot');
	src: url('./assets/font/NanumBarunGothic.eot') format('embedded-opentype'),
	url('./assets/font/NanumBarunGothic.woff') format('woff');
}

#app {
	font-family: 'NanumBarunGothic', 'serif';
	position:relative;
	min-height:100%;
}

#wrappers > div{
	height: 100%;
}

.message-box {
	font-weight: 900;
}

.datagrid-td {
	cursor: pointer;
}
</style>
