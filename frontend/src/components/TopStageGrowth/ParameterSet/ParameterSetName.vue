<template>
  <v-container>
    <v-dialog v-model="isDialog" width="600">
      <v-card>
        <v-card-title v-if="title != null" class="text-h5">
          {{ parameterName }}
        </v-card-title>
        <v-form validate-on="submit" @submit.prevent="submit">
          <v-container>
            <v-row>
              <v-col cols="5">
                <v-card-text> 保存するパラメータセット名 </v-card-text>
              </v-col>
              <v-col cols="7">
                <v-text-field v-model="parameterSetName" :rules="nameRules" required />
              </v-col>
            </v-row>
            <v-card-actions style="text-align: center" width="100%">
              <div class="GS_ButtonArea">
                <div class="text-center">
                  <v-btn type="submit" color="primary" blocak class="ma-2">
                    {{ confirmText }}
                  </v-btn>
                  <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="close()">
                    {{ cancelText }}
                  </v-btn>
                </div>
              </div>
            </v-card-actions>
          </v-container>
        </v-form>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import { MountController } from "@/lib/mountController.js";

export default {
  props: {
    shared: {
      type: MountController,
      required: false,
    },
    title: {
      type: String,
      required: false,
    },
    show: {
      type: Boolean,
      default: false,
      required: false,
    },
    handleSubmit: {
      type: Function,
      required: false,
    },
    handleClose: {
      type: Function,
      required: false,
    },
    confirmText: {
      type: String,
      default: "確認",
      required: false,
    },
    cancelText: {
      type: String,
      default: "取消",
      required: false,
    },
  },

  data() {
    return {
      isDialog: this.show,
      modelItem: null,
      // sharedGraph: new MountController(),
      // valid: false,
      parameterName: "",
      nameRules: [
        (value) => {
          if (value) return true;
          return "パラメータ名入力は必須です。";
        },
      ],
    };
  },

  components: {},

  mounted() {
    this.shared.mount(this);
  },
  updated() { },
  methods: {
    initialize: function (selectedInfo, arg) {
      this.modelItem = arg;
      this.selectedInfo = selectedInfo;
    },

    submit: async function () {
      this.isDialog = false;
      if (this.handleSubmit != null) {
        this.handleSubmit(this.parameterSetName);
      }
    },
    close: function () {
      this.isDialog = false;
      this.parameterSetName = "";
    },
    display: function () {
      this.isDialog = true;
      this.parameterSetName = "";
    },
  },
};
</script>
