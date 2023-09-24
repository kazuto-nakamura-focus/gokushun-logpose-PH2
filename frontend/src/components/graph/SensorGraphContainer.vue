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
import Ph2GraphArea from "@/components/GrowthModel/Ph2GraphArea.vue";
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
      //* ============================================
      //* 選択されたターゲットとグラフの関係
      //* ============================================
      DISPLAYED: {
        map: new Map(), // 表示済みMAP
        //* --------------------------------------------
        //* 作成されたグラフの登録
        //* --------------------------------------------
        add(selectedItems) {
          let key =
            selectedItems.selectedModel.id +
            "+" +
            selectedItems.selectedField.id +
            "+" +
            selectedItems.selectedDevice.id +
            "+" +
            selectedItems.selectedYear.id;
          this.map.set(key, {
            items: selectedItems,
            node: null,
          });
        },
      },
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
    setGraphData: function (titlePaths, contentId, sensorId, startDate, endDate, type, hour) {
      this.isLoading = true;
      this.$nextTick(
        function () {
          this.setSonsorData(titlePaths, contentId, sensorId, startDate, endDate, type, hour);
        }.bind(this)
      );
    },
    setSonsorData(titlePaths, contentId, sensorId, startDate, endDate, type, hour) {
      // センサーのグラフデータを取得する
      useSensoreData(sensorId, startDate, endDate, type, hour)
        .then((response) => {
          // 成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            // グラフの表示オプションを設定
            let gc = new SensorChart();
            gc.setOptions(titlePaths, this.yTitle[contentId-1].text, data);
            // グラフ表示を行う
            gc.setLoadingParent(this);
            this.$refs.chr.addGraph(
              titlePaths,
              gc.data.chartOptions,
              data.values,
              false
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
  
