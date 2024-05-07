<!--パラメーターセット表示-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" permanent max-width="700px">
        <v-card>
          <v-container>
            <toggle-button
              v-show="tabModel == 'tab-1'"
              :value="false"
              :checked="isEditMode"
              v-model="isEditMode"
              :width="125"
              :height="27"
              :labels="{ checked: '編集モード', unchecked: '表示モード' }"
              @change="onToggleChange($event)"
            ></toggle-button>
            <v-tabs v-model="tabModel">
              <v-tab href="#tab-1">パラメータセット名</v-tab>
              <!--  <v-tab href="#tab-2" v-show="modelId==1">適用グラフ</v-tab>-->
              <v-tab href="#tab-3">履歴</v-tab>
              <v-tab-item value="tab-1">
                <v-card-text>
                  <div class="modal-tab">
                    <parameter-set-tag
                      ref="refParameterSet"
                      :shared="sharedInput"
                      :modelId="modelItem"
                      @changeItem="changeItem"
                      @reset="setData"
                      @close="close"
                    />
                  </div>
                </v-card-text>
              </v-tab-item>
              <!--<v-tab-item value="tab-2" v-if="modelId == 1">
                <ParameterSetGraph
                  ref="refParameterSetGraph"
                  :selectParameterSetName="selectParameterSetName"
                  :fieldName="fieldName"
                  :fieldId="fieldId"
                  :year="year"
                />
              </v-tab-item>-->
              <v-tab-item value="tab-3">
                <parameter-set-history ref="refHistory"></parameter-set-history>
              </v-tab-item>
            </v-tabs>
          </v-container>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>

<script>
import { MountController } from "@/lib/mountController.js";
//import { mdiExitToApp } from "@mdi/js";
import ParameterSetTag from "@/components/TopStageGrowth/ParameterSet/ParameterSetTag.vue";
//import ParameterSetGraph from "./Graph/index.vue";
import ParameterSetHistory from "@/components-v1/parts/パラメータセット編集/履歴.vue";
import { useGrowthParamDefaultId } from "@/api/TopStateGrowth/GEParameterSets";

export default {
  props: {
    shared /** MountController */: { required: true },
    modelId: { required: true },
  },
  data() {
    return {
      isDialog: false,
      modelItem: this.modelId,
      sharedInput: new MountController(),
      tabModel: "tab-1",
      defaultId: null,
      isEditMode: false,
    };
  },

  components: {
    //ParameterSetGraph,
    ParameterSetHistory,
    ParameterSetTag,
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    //*----------------------------
    // 初期表示を行う
    //*----------------------------
    initialize: function (selectedInfo) {
      //* 選択されたデバイスや年度など
      this.selectedInfo = selectedInfo;
      //* モデル
      this.modelItem = selectedInfo.menu.selectedModel.id;
      //* 下位パネルにマウント指示
      this.$nextTick(
        function () {
          this.setData();
        }.bind(this)
      );
    },
    setData() {
      //* デフォルトのパラメータセットを取得する
      useGrowthParamDefaultId(
        this.modelId,
        this.selectedInfo.menu.selectedDevice.id,
        this.selectedInfo.menu.selectedYear.id
      )
        .then((response) => {
          this.defaultId = response.data.data;
          this.$nextTick(function () {
            this.$refs.refParameterSet.initialize(
              this.defaultId,
              this.selectedInfo.menu
            );
            this.$refs.refHistory.initialize(this.defaultId);
          });
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //*----------------------------
    // 表示パラメータの変更を行う
    //*----------------------------
    changeItem: function (selectedId) {
      if (null == selectedId) selectedId = this.defaultId;
      this.$refs.refHistory.initialize(selectedId);
    },
    //*----------------------------
    // 編集モードの変更を取得する
    //*----------------------------
    onToggleChange: function (event) {
      this.$refs.refParameterSet.setEditable(event.value);
    },
    //*----------------------------
    // 画面を閉じる
    //*----------------------------
    close: function (status) {
      this.isDialog = false;
      this.shared.onConclude(status);
    },
  },
};
</script>

<style lang="scss">
@import "@/style/common.css";

.theme--light.v-text-field--solo > .v-input__control > .v-input__slot {
  background: #f4f5fa;
}

.modal-tab {
  background-color: white;
  padding: 5px;
  text-align: left;
}

.action-button {
  border: none;
  color: black;
  padding: 3px 12px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  opacity: 0.7;
  background-color: white;
}
</style>
