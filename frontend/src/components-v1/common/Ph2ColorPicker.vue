<template>
  <v-dialog v-model="isDialog" width="300px">
    <v-card>
      <v-card-title class="dialog-warning-header">
        <div>アノテーションの線の色を選択してください。</div>
      </v-card-title>
      <v-card-text class="data-contents">
        <v-color-picker v-model="color"></v-color-picker>
      </v-card-text>
      <div class="GS_ButtonArea">
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="doAction(true)"
          >更新</v-btn
        >
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="doAction(false)"
          >キャンセル</v-btn
        >
      </div>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: { shared /** DialogController */: { required: true } },

  data() {
    return {
      isDialog: false,
      color: "",
    };
  },
  // **********************************************************
  // DOM作成後
  // **********************************************************
  mounted() {
    this.shared.mount(this);
  },
  // **********************************************************
  // 関数
  // **********************************************************
  methods: {
    // ======================================================
    // データの初期化
    // ======================================================
    initialize(color) {
      this.color = color;
    },
    // ======================================================
    // ボタン選択時
    // ======================================================
    doAction(mode) {
      this.isDialog = false;
      if (mode) this.shared.onConclude(this.color);
    },
  },
};
</script>