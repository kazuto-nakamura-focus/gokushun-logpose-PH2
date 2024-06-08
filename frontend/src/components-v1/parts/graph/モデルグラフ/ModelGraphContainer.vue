<template>
  <v-app>
    <v-container>
      <Ph2GraphTabs graphType="1" ref="chr" @doAction="setData" />
      <wait-dialog ref="wait" />
    </v-container>
  </v-app>
</template>
  
<script>
import Ph2GraphTabs from "@/components-v1/parts/graph/Ph2GraphTab.vue";
import {
  useModelData,
  useLeafGraphAreaByParamSet,
  usePhotosynthesisGraphByParamSet,
} from "@/api/TopStateGrowth";
import WaitDialog from "@/components-v1/parts/dialog/WaitDialog.vue";
import { ModelGraphSettings } from "@/components-v1/parts/graph/モデルグラフ/モデルグラフ設定.js";
import { GraphPanel } from "@/components-v1/parts/graph/モデルグラフ/グラフパネル設定.js";

export default {
  data() {
    return {
      modelId: 0,
    };
  },
  components: {
    Ph2GraphTabs,
    WaitDialog,
  },
  methods: {
    //* --------------------------------------------
    //* グラフデータ生成
    //* --------------------------------------------
    setGraphData: function (selectedItems) {
      this.$nextTick(
        function () {
          this.setData(selectedItems);
        }.bind(this)
      );
    },
    //* ============================================
    // グラフ描画処理の呼び出し
    //* ============================================
    callGraphPanel(data, selectedItems, settings) {
      // モデルパネルオブジェクト
      let graphPanel = new GraphPanel();
      // パネルタイトルの作成
      graphPanel.setPanelTitle(selectedItems);
      // パネルコメントの作成
      graphPanel.setComment(data);
      // グラフデータの作成
      graphPanel.setGraphData(settings);

      let graphId = this.$refs.chr.addGraph(graphPanel);
      // グラフのIDリストに追加し、同じグラフが追加される場合は削除できるようにする。
      selectedItems.selectedYear.graphId.push(graphId);
    },
    //* ============================================
    // グラフデータを取得し、表示する
    //* ============================================
    setData(selectedItems) {
      // * グラフがすでに描画済みであるならば、削除する
      if (selectedItems.selectedYear.graphId === undefined) {
        selectedItems.selectedYear.graphId = [];
      } else {
        for (const id of selectedItems.selectedYear.graphId) {
          this.$refs.chr.deleteItem(id);
        }
      }

      this.modelId = selectedItems.selectedModel.id;

      // モデルグラフオブジェクト
      let modelGraphSettings = new ModelGraphSettings();
      let year = selectedItems.selectedYear.id;
      let deviceId = selectedItems.selectedDevice.id;
      // モデルによって表示を変更する
      if (this.modelId == 1) {
        this.$refs.wait.start("描画中です。しばらくお待ちください。", false);
        useModelData(deviceId, year)
          .then((response) => {
            // 成功時
            const results = response["data"].data;
            if (results == null) {
              alert("データがありません。");
              return;
            }
            // グラフデータの作成
            modelGraphSettings.setGrowthGraph(results);
            // グラフパネルの呼び出し
            this.callGraphPanel(results, selectedItems, modelGraphSettings);
          })
          .catch((error) => {
            console.log(error);
          })
          .finally(() => {
            this.$refs.wait.finish();
          });
      } else if (this.modelId == 2) {
        this.$refs.wait.start("描画中です。しばらくお待ちください。", false);
        useLeafGraphAreaByParamSet(deviceId, year)
          .then((response) => {
            const results = response["data"].data;
            if (results == null) {
              alert("データがありません。");
              return;
            }
            // グラフデータの作成
            modelGraphSettings.setLeafAreaGraph(results[0], results[1]);
            // グラフパネルの呼び出し
            this.callGraphPanel(results, selectedItems, modelGraphSettings);
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          })
          .finally(() => {
            this.$refs.wait.finish();
          });
      } else if (this.modelId == 3) {
        this.$refs.wait.start("描画中です。しばらくお待ちください。", false);
        usePhotosynthesisGraphByParamSet(deviceId, year)
          .then((response) => {
            //成功時
            const results = response["data"].data;
            if (results == null) {
              alert("データがありません。");
              return;
            }
            // グラフデータの作成
            modelGraphSettings.setPhotoSynthesysGraph(results);
            // グラフパネルの呼び出し
            this.callGraphPanel(results, selectedItems, modelGraphSettings);
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          })
          .finally(() => {
            this.$refs.wait.finish();
          });
      }
    },
  },
};
</script>
  
