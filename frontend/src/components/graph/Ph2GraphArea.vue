<template>
  <div>
    <div>
      <ph-2-graph-component
        v-for="item in graphList"
        :key="item.id"
        :target="item"
      />
    </div>
    <wait-dialog ref="wait" @run="addGraphImpl" />
  </div>
</template>
<script>
import Ph2GraphComponent from "./Ph2GraphComponent.vue";
import WaitDialog from "@/components/dialog/WaitDialog.vue";

export default {
  //* ============================================
  // 使用コンポーネントリスト
  //* ============================================
  components: {
    Ph2GraphComponent,
    WaitDialog,
  },
  //* ============================================
  // データ
  //* ============================================
  data() {
    return {
      graphList: [], // グラフデータリスト
      item:{},
    };
  },
  methods: {
    //* ============================================
    // グラフ追加処理
    //* ============================================
    addGraph: function (titlePath, chartOptions, chartData, isMultiple) {
      this.item = {
          id: this.graphList.length, // ID
          title: titlePath, // グラフタイトル
          options: chartOptions, // グラフオプション
          data: chartData, // グラフデータ
          isMultiple: isMultiple, // 単一グラフか複数グラフか
        };
       this.$refs.wait.start("描画を開始します。");
       new Promise(this.addGraphImpl());
    },
    addGraphImpl : function(){
        // * リストに追加することでグラフ表示
        this.graphList.push(this.item);
        this.$refs.wait.finish();
      }
  },
};
</script>