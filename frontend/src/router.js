import Vue from "vue";
import Router from "vue-router";
import Top from "@/components/Top";
//import Login from '@/components/Login.vue'
import TopGraphs from "@/components/SensorGraph.vue";
import GrowthModel from "@/components/GrowthModel";
//import TopStageGrowthOld from "@/components/TopStageGrouth";
//import TopStageGrowth from "@/components/TopStageGrowth";
import RawDataView from "@/components/RawDataView.vue";

import ManagementScreenTop from "@/components/ManagementScreenTop";
import Login from "@/components/Login/login.vue";

import store from "./store";

Vue.use(Router);

const router = new Router({
  mode: "history",
  routes: [
    //ログイン画面
    { path: "/login", name: "login", component: Login, meta: { requiresAuth:false } },
    // トップ画面
    { path: "/", name: "top", component: Top, meta: { requiresAuth: true } },
    // 成長予測画面
    {
      path: "/top_growth",
      name: "growth",
      component: GrowthModel,
      meta: { requiresAuth: true },
    },
    //      { path: '/login', component: Login },
    // グラフ画面
    {
      path: "/top_graphs",
      name: "graph",
      component: TopGraphs,
      meta: { requiresAuth: true },
    },
    // { path: "/top_graphs", name: "graph", component: TopStageGrowthOld },
    // 管理画面
    {
      path: "/management_screen_top",
      name: "management_screen_top",
      component: ManagementScreenTop,
      meta: { requiresAuth: true },
    },
    // 生データ画面
    {
      path: "/top_data",
      name: "data",
      component: RawDataView,
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth != null)) {
    // セッション情報の確認が必要なルートの場合
    store.dispatch('checkSession')
      .then(isLoggedIn => {
        const path = to["path"];
        console.log(path.includes("/login"), isLoggedIn);
        
        if(path.includes("/login")){
          if(!isLoggedIn){
            next();
          }else{
            next("/");
          }
        }else if (!isLoggedIn) {
          alert("ログインセッションがタイムアウトしました。再ログインしてください");
          next('/login');
        } else {
          next();
        }
      });
  } else {
    next(); // 認証が必要ないルートにはそのまま進む
  }
});

// router.beforeEach((to, from, next) => {
//   console.log("router.beforeEach");
//   console.log(to, from, next)

//   const isLoading = store.getters.isLoading;
//   const isLoggedIn = store.getters.isLoggedIn;

//   console.log("isLoading", isLoading);
//   console.log("isLoggedIn", isLoggedIn);

//   if (to.matched.some((record) => record.meta.requiresAuth)) {
//     // ここでセッションの確認を行う
//     if (isLoggedIn) {
//       next();
//     } else {
//       next("/login");
//     }
//   } else {
//     if(isLoggedIn){
//       next("/");
//     }
//   }
// });

export default router;
