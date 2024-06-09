<template>
  <v-app>
    <v-container>
      <Ph2GraphArea graphType="2" ref="chr" />
    </v-container>

    <div class="modal-mask" v-if="isLoading">
      <div style="text-align: center">描画中です。</div>
      <div class="loading">
        <vue-loading
          type="spin"
          color="#ff0"
          :size="{ width: '50px', height: '50px' }"
        ></vue-loading>
      </div>
    </div>
    <wait-dialog ref="wait" />
  </v-app>
</template>
  
  <script>
import { RawDataSettings } from "@/components-v1/parts/graph/生データグラフ/生データグラフ設定.js";
import { RawDataGraphPanel } from "@/components-v1/parts/graph/生データグラフ/生データパネル設定.js";
import Ph2GraphArea from "@/components-v1/parts/graph/Ph2GraphTab.vue";
import { VueLoading } from "vue-loading-template";
import { useSensoreData } from "@/api/SensorDataAPI.js";

import WaitDialog from "@/components-v1/parts/dialog/WaitDialog.vue";

export default {
  data() {
    return {
      isLoading: false, // ローダー

      yTitle: [
        { text: "温度(℃)" },
        { text: "湿度(％RH)" },
        { text: "日射(W/㎡)" },
        { text: "樹液流1(g/h)" },
        { text: "デンドロ(μm)" },
        { text: "葉面濡れ(raw counts)" },
        { text: "土壌水分(pF)" },
        { text: "土壌温度(℃)" },
        { text: "体積含水率(vol%)" },
        { text: "樹液流2(g/h)" },
      ],

      xTitle: [{ text: "" }],
    };
  },
  components: {
    Ph2GraphArea,
    VueLoading,
    WaitDialog,
  },
  methods: {
    //* --------------------------------------------
    //* グラフデータ生成
    //* --------------------------------------------
    setGraphData: function (
      titlePaths,
      contentId,
      sensorId,
      startDate,
      endDate,
      interval,
      name,
      title
    ) {
      this.isLoading = true;
      this.$nextTick(
        function () {
          this.setSonsorData(
            titlePaths,
            contentId,
            sensorId,
            startDate,
            endDate,
            interval,
            name,
            title
          );
        }.bind(this)
      );
    },
    setSonsorData(
      titlePaths,
      contentId,
      sensorId,
      startDate,
      endDate,
      interval,
      name,
      title
    ) {
      this.$refs.wait.start("描画中です。しばらくお待ちください。", false);
      useSensoreData(sensorId, startDate, endDate, interval)
        .then((response) => {
          // 成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            // グラフの表示オプションを設定
            let settings = new RawDataSettings();
            settings.setGrowthGraph(
              title,
              this.xTitle[0].text,
              this.yTitle[contentId - 1].text,
              data
            );
            console.log("aaa");
            // モデルパネルオブジェクト
            let graphPanel = new RawDataGraphPanel();
            // パネルタイトルの作成
            graphPanel.setPanelTitle(titlePaths);
            // グラフデータの作成
            graphPanel.setGraphData(settings);
            // タブに追加する
            this.$refs.chr.addGraph(graphPanel);
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        })
        .finally(() => {
          this.$refs.wait.finish();
        });
    },
  },
};
</script>
  
