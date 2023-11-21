<template>
  <div class="text-center">
    <v-container>
      <v-expansion-panels multiple v-model="panelIndexes">
        <v-expansion-panel v-for="(item, i) in devices" :key="i">
          <v-expansion-panel-header>
            <v-checkbox
              v-model="item.isDisplay"
              :label="item.deviceName"
              dense
              @click="clicked(item)"
            ></v-checkbox>
          </v-expansion-panel-header>

          <v-expansion-panel-content>
            <senors-setting
              v-if="devices[i].hasData == true"
              :deviceId="devices[i].deviceId"
            ></senors-setting>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-container>
  </div>
</template>
  
<script>
import { useUpdateDisplay } from "@/api/DashboardAPI.js";
import SenorsSetting from "./SenorsSetting.vue";
export default {
  props: {
    devices: {
      type: Object, // 型がObjectである
      required: true, // 必須のプロパティである
    },
  },
  data: () => ({
    deviceList: [],
    panelIndexes: [],
  }),
  mounted() {
    for (const item of this.devices) {
      item.hasData = new Object();
      item.hasData = false;
      this.deviceList.push(item);
    }
  },
  components: {
    SenorsSetting,
  },
  watch: {
    panelIndexes: function () {
      for (const index of this.panelIndexes) {
        this.deviceList[index].hasData = true;
      }
    },
  },
  methods: {
    clicked(item) {
      const arg = {
        deviceId: item.deviceId,
        isDisplay: item.isDisplay,
      };
      this.callUseUpdateDisplayAPI(arg);
    },
    //* ============================================
    // APIのコールとデータの設定-表示設定
    //* ============================================
    callUseUpdateDisplayAPI(arg) {
      useUpdateDisplay(arg)
        .then((response) => {
          const results = response["data"];
          //* 結果データの検証
          if (results.status != 0) {
            console.log(results.message);
            alert("表示設定に失敗しました。");
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
  },
};
</script>