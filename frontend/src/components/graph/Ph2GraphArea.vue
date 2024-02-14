<template>
  <div>
    <div>
      <ph-2-graph-component
        v-for="item in graphList"
        :key="item.id"
        :target="item"
        @delete="deleteItem"
      />
    </div>
    <!-- <wait-dialog ref="wait" /> -->
  </div>
</template>
<script>
import Ph2GraphComponent from "./Ph2GraphComponent.vue";
// import WaitDialog from "@/components/dialog/WaitDialog.vue";

export default {
  //* ============================================
  // 使用コンポーネントリスト
  //* ============================================
  components: {
    Ph2GraphComponent,
    // WaitDialog,
  },
  //* ============================================
  // データ
  //* ============================================
  data() {
    return {
      id: 0,
      graphList: [], // グラフデータリスト
      item: {},
    };
  },
  methods: {
    //* ============================================
    // グラフ追加処理
    //* ============================================
    addGraph: function (titlePath, chartOptions, chartData, isMultiple, name) {
    let item = {
        id: this.id++, // ID
        title: titlePath, // グラフタイトル
        options: chartOptions, // グラフオプション
        data: chartData, // グラフデータ
        isMultiple: isMultiple, // 単一グラフか複数グラフか
        name: name, // グラフ線の名前
      };
      this.displayGraph(item);
    },

    displayGraph(item) {
      this.graphList.push(item);
    },
    //* ============================================
    // グラフアイテムの削除
    //* ============================================
    deleteItem: function (id) {
      this.graphList.forEach((item, index) => {
        if (item.id == id) {
          this.graphList.splice(index, 1);
        }
      });
    },
  },
};
</script>