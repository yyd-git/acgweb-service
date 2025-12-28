import Vue from 'vue'
import VueRouter from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ProductListView from '../views/ProductListView.vue'
import ProductView from '../views/ProductView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },

  {
    path: '/products',
    name: 'products',
    component: ProductListView
  },
  {
    path: '/products/:id',
    name: 'product-detail',
    component: ProductView,
    props: true, // 允许通过 props 接收 id
  },
  {
    path: '/',
    redirect: '/login'
  }

]

const router = new VueRouter({
  mode: 'history', // 可选（推荐）
  routes
})

export default router
