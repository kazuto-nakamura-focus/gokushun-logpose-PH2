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

Vue.use(Router);

export default new Router({
  mode: "history",
  routes: [
    // トップ画面
    { path: "/", name: "top", component: Top },
    // 成長予測画面
    { path: "/top_growth", name: "growth", component: GrowthModel },
    //      { path: '/login', component: Login },
    // グラフ画面
      { path: "/top_graphs", name: "graph", component: TopGraphs },
   // { path: "/top_graphs", name: "graph", component: TopStageGrowthOld },
    // 管理画面
    { path: "/management_screen_top", name: "management_screen_top", component: ManagementScreenTop},
    // 生データ画面
    { path: "/top_data", name: "data", component: RawDataView },
  ],
});
