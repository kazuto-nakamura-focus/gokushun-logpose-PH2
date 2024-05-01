<!--葉面積実績値入力-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="900" persistent>
        <v-card>
          <v-card-title>実績値入力</v-card-title>
          <!-- タイトル部分 -->
          <input-header ref="titleHeader" />
          <leaf-count-input ref="leafCountInput" v-if="target!=null" :target="target"></leaf-count-input>
          <leaf-area-input ref="leafAreaInput" v-if="target!=null" :target="target" @close="close"></leaf-area-input>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>
<script>
import InputHeader from "@/components/TopStageGrowth/actualValueInput/InputHeader.vue";
import leafCountInput from "@/components-v1/LeafModel/RealValueInput/新梢数更新.vue";
import leafAreaInput from "@/components-v1/LeafModel/RealValueInput/葉面積葉枚数更新.vue";

export default {
  props: {
    shared /** MountController */: { required: true },
  },

  components: {
    leafCountInput,
    InputHeader,
    leafAreaInput,
  },

  data() {
    return {
      isDialog: false,
      target: null,
    };
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    //* ============================================
    // 初期化・表示初期値
    //* ============================================
    initialize: function (data) {
      this.$nextTick(function () {
        this.$refs.titleHeader.initialize(data.menu);
      });
      //* 各コンポーネントへのプロパティの設定
      this.target = {
        deviceId: data.menu.selectedDevice.id,
        year: data.menu.selectedYear.id,
      };
    },
    //* ============================================
    // 画面を閉じる
    //* ============================================
    close() {
      this.isDialog = false;
      this.shared.onConclude(
        this.$refs.leafCountInput.isUpdated ||
          this.$refs.leafAreaInput.isUpdated
      );
    },
  },
};
</script>
<style>
</style>
