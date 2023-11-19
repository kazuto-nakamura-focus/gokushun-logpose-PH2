<template>
  <v-container>
    <v-expansion-panels focusable multiple>
      <v-expansion-panel v-for="(field, i) in fieldItems" :key="i">
        <v-expansion-panel-header>{{ field.name }}</v-expansion-panel-header>
        <v-expansion-panel-content>
          <device-setting :devices="field.devices"> </device-setting>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>
</template>
<script>
import { useDeviceTarget } from "@/api/DashboardAPI.js";
import DeviceSetting from "./DeviceSetting.vue";
export default {
  components: { DeviceSetting },
  data: () => ({
    fieldItems: [],
  }),
  mounted() {
    this.initialize();
  },
  methods: {
    //* ============================================
    // APIのコールとデータの設定-フィールドリストの取得
    //* ============================================
    initialize() {
      useDeviceTarget()
        .then((response) => {
          const results = response["data"];
          //* 結果データの検証
          if (results.status != 0) {
            console.log(results.message);
            alert("フィールドリストの取得に失敗しました。");
          } else {
            this.fieldItems = results.data;
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
  },
};
</script>