import Vue from 'vue'
import './plugins/axios'
import 'es6-promise/auto'
import App from './App'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import locale from 'element-ui/lib/locale/lang/en'
import VueApexCharts from 'vue-apexcharts'
import Util from './util.js'
import 'element-ui/lib/theme-chalk/index.css'
import 'vx-easyui/dist/themes/gray/easyui.css'
import 'vx-easyui/dist/themes/icon.css'
import 'vx-easyui/dist/themes/vue.css'
import EasyUI from 'vx-easyui'

Vue.use(ElementUI, { locale })
Vue.use(EasyUI)
Vue.use(VueApexCharts)
Vue.use(Util)
Vue.component('apexchart', VueApexCharts)

Vue.config.productionTip = false

new Vue({
	el: '#app',
	router,
	store,
	components: { App },
	template: '<App/>'
})
