import Vue from 'vue'
import Router from 'vue-router'
import Action from '../components/Action.vue'

Vue.use(Router)

export default new Router({
	mode: 'history',
	routes: [
		{
			path: '/',
			component: Action
		},
		{
			path: '/action',
			component: Action
		},
		{
			path: '/scenario',
			component: () => import('../components/Scenario.vue')
		},
		{
			path: '/reservation',
			component: () => import('../components/Reservation.vue')
		},
		{
			path: '/statistics',
			component: () => import('../components/Statistics.vue')
		},
		{
			path: '/totalInfo',
			component: () => import('../components/TotalInfo.vue')
		},
		{
			path: '/chart',
			component: () => import('../components/Chart.vue')
		}
	]
})
