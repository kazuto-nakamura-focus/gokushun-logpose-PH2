<template>
  <v-app>
    <v-container>
      <Ph2GraphArea ref="chr" />
    </v-container>

    <div class="modal-mask" v-if="isLoading">
      <div style="align: center">描画中です。</div>
      <div class="loading">
        <vue-loading
          type="spin"
          color="#ff0"
          :size="{ width: '50px', height: '50px' }"
        ></vue-loading>
      </div>
    </div>
  </v-app>
</template>
  
  <script>
import { SensorChart } from "@/lib/graph/ApexCharts/SensorChart.js";
import Ph2GraphArea from "./Ph2GraphArea.vue";
import { VueLoading } from "vue-loading-template";
import { useSensoreData } from "@/api/SensorDataAPI.js";

export default {
  data() {
    return {
      selectedItem: {},
      isLoading: false, // ローダー

      yTitle: [
        { text: "温度(℃)"},
        { text: "湿度(％RH)"},
        { text: "日射(W/㎡)" },
        { text: "樹液流1(g/h)" },
        { text: "デンドロ(μm)" },
        { text: "葉面濡れ(raw counts)" },
        { text: "土壌水分(pF)" },
        { text: "土壌温度(℃)" },
        { text: "日射(μmol/㎡s)" },
        { text: "樹液流2(g/h)"},
      ],
    };
  },
  components: {
    Ph2GraphArea,
    VueLoading,
  },
  methods: {
    //* --------------------------------------------
    //* グラフデータ生成
    //* --------------------------------------------
    setGraphData: function (titlePaths, contentId, sensorId, startDate, endDate, interval, name, title) {
      this.isLoading = true;
      this.$nextTick(
        function () {
          this.setSonsorData(titlePaths, contentId, sensorId, startDate, endDate, interval, name, title);
        }.bind(this)
      );
    },
    setSonsorData(titlePaths, contentId, sensorId, startDate, endDate, interval, name, title) {
      useSensoreData(sensorId, startDate, endDate, interval)
        .then((response) => {
          // 成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            // グラフの表示オプションを設定
            let gc = new SensorChart();
            gc.setOptions(title, this.yTitle[contentId-1].text, data);
            console.log(data.category);
            // グラフ表示を行う
            gc.setLoadingParent(this);
            this.$refs.chr.addGraph(
              titlePaths,
              gc.data.chartOptions,
              data.values,
              false,
              name
            );
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
  },
};
</script>
  
