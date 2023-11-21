<!-- 管理のトップ画面（高橋さんが制作したDashboard画面 -->
<template>
  <v-app>
    <v-container>
      <v-card elevation="0">
        <v-tabs v-model="tabModel">
          <v-tab v-for="tab in tabs" :key="tab.value" :href="tab.href">{{
            tab.name
          }}</v-tab>
          <v-tab-item value="tab-1">
            <MSField />
          </v-tab-item>
          <v-tab-item value="tab-2">
            <MSDevice />
          </v-tab-item>
          <v-tab-item value="tab-3">
            <MSDashboard />
          </v-tab-item>
        </v-tabs>
      </v-card>
    </v-container>
  </v-app>
</template>
<script>
import MSDashboard from "./MSDashboard/DashboardSetting.vue";
import MSField from "./MSField";
import MSDevice from "./MSDevice";
const TEST_TABS = [
  {
    href: "#tab-1",
    value: "tab-1",
    name: "圃場管理",
  },
  {
    href: "#tab-2",
    value: "tab-2",
    name: "デバイス管理",
  },
  {
    href: "#tab-3",
    value: "tab-3",
    name: "ダッシュボード",
  },
];
export default {
  props: {},
  data() {
    return {
      tabs: TEST_TABS,
      tabModel: "tab-1",
    };
  },
  components: {
    MSDashboard,
    MSField,
    MSDevice,
  },
  mounted() {},
  methods: {},
};
</script>