<template>
  <v-app>
    <v-container>
      <Ph2GraphArea graphType="2" ref="chr" />
    </v-container>
  </v-app>
</template>
  
  <script>
import { RawDataSettings } from "@/components-v1/parts/graph/生データグラフ/生データグラフ設定.js";
import { RawDataGraphPanel } from "@/components-v1/parts/graph/生データグラフ/生データパネル設定.js";
import Ph2GraphArea from "@/components-v1/parts/graph/Ph2GraphTab.vue";
import { useSensoreData } from "@/api/SensorDataAPI.js";
import { LabelFlags } from "@/components-v1/parts/graph/カテゴリー.js";

export default {
  data() {
    return {
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
      flags: null,
    };
  },
  components: {
    Ph2GraphArea,
  },
  methods: {
    //* --------------------------------------------
    //* グラフデータ生成
    //* --------------------------------------------
    setGraphData: function (
      titlePaths,
      deviceId,
      contentId,
      sensorId,
      startDate,
      endDate,
      interval,
      name,
      title
    ) {
      this.$nextTick(
        function () {
          this.setSonsorData(
            titlePaths,
            deviceId,
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
      deviceId,
      contentId,
      sensorId,
      startDate,
      endDate,
      interval,
      name,
      title
    ) {
      useSensoreData(deviceId, sensorId, startDate, endDate, interval)
        .then((response) => {
          // 成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            // グラフの表示オプションを設定
            let settings = new RawDataSettings();
            data.flags = new Object();
            data.flags = this.flags.flags;
            settings.setGrowthGraph(
              title,
              this.xTitle[0].text,
              this.yTitle[contentId - 1].text,
              data
            );
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
        });

      this.flags = new LabelFlags();
      this.flags.getSensorGraphDataByInterval(startDate, endDate, interval);
    },
  },
};
</script>
  
