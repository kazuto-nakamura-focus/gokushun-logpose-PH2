<template>
  <v-container>
    <v-dialog v-model="isDialog" width="600" persistent>
      <v-card>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-text-field
                label="新しいパラメータセット名"
                background-color="#F4FCE0"
                v-model.trim="parameterSetName"
              />
            </v-col>
          </v-row>
          <v-card-actions style="text-align: center" width="100%">
            <div class="GS_ButtonArea">
              <div class="text-center">
                <v-btn
                  type="submit"
                  color="primary"
                  blocak
                  class="ma-2"
                  :disabled="parameterSetName.length==0"
                  @click="save()"
                >保存</v-btn>
                <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="close()">キャンセル</v-btn>
              </div>
            </div>
          </v-card-actions>
        </v-container>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      isDialog: true,
      parameterSetName: "",
    };
  },

  components: {},
  mounted() {},
  updated() {},
  methods: {
    initialize: function (selectedInfo, arg) {
      this.parameterSetName = "";
      this.modelItem = arg;
      this.selectedInfo = selectedInfo;
    },
    save: function () {
      this.isDialog = false;
      this.$emit("submit", this.parameterSetName);
    },
    close: function () {
      this.isDialog = false;
    },
  },
};
</script>
