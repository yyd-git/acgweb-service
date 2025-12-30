import Vue from "vue";
import VueRouter from "vue-router";

import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import ProductListView from "../views/ProductListView.vue";
import ProductView from "../views/ProductView.vue";

Vue.use(VueRouter);

const routes = [
  {
    path: "/login",
    name: "login",
    component: LoginView,
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
  },
  {
    path: "/products",
    name: "products",
    component: ProductListView,
    meta: { requiresAuth: true }, // 🔐 需要登录
  },
  {
    path: "/products/:id",
    name: "product-detail",
    component: ProductView,
    props: true,
    meta: { requiresAuth: true }, // 🔐 需要登录
  },
  {
    path: "/",
    redirect: "/login",
  },
];

const router = new VueRouter({
  mode: "history",
  routes,
});

/**
 * ===============================
 * 全局前置导航守卫
 * ===============================
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");

  // 判断目标路由是否需要登录
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!token) {
      // ❌ 未登录，强制跳转登录页
      next({
        path: "/login",
        query: { redirect: to.fullPath }, // 登录后跳回原页面
      });
    } else {
      // ✅ 已登录，放行
      next();
    }
  } else {
    // 不需要登录的页面，直接放行
    next();
  }
});

export default router;
