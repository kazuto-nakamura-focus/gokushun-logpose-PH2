<!--パラメーターセット表示-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" persistent max-width="700px">
        <v-card>
          <v-container>
            <!-- タイトル部分 -->
            <input-header ref="titleHeader" />

            <v-tabs v-model="tabModel" @change="tabChanged">
              <v-tab href="#tab-1">パラメータセット編集</v-tab>
              <!--  <v-tab href="#tab-2" v-show="modelId==1">適用グラフ</v-tab>-->
              <v-tab href="#tab-3">履歴</v-tab>
              <v-tab-item value="tab-1">
                <div style="margin-top:10px">
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
                </div>
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
                <parameter-set-history ref="refHistory" @mounted="tabChanged"></parameter-set-history>
              </v-tab-item>
            </v-tabs>
          </v-container>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>

<script>
import InputHeader from "@/components-v1/parts/変数入力画面ヘッダー.vue";
import { MountController } from "@/lib/mountController.js";
import ParameterSetTag from "@/components-v1/parts/パラメータセット編集/編集タブ共通.vue";
import ParameterSetHistory from "@/components-v1/parts/パラメータセット編集/履歴タブ.vue";
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
      selectedId: null,
    };
  },

  components: {
    InputHeader,
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
      this.$nextTick(function () {
        this.$refs.titleHeader.initialize(selectedInfo.menu);
      });
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
      this.selectedId = selectedId;
    },
    //*----------------------------
    // タブ変更時の処理
    //*----------------------------
    tabChanged: function () {
      // * 履歴タブへの変更
      if (this.tabModel == "tab-3") {
        // * タブを変えた時点ではRefsは認識できないので、履歴部分のマウントを待つ
        if (this.$refs.refHistory !== undefined) {
          if (null == this.selectedId) this.selectedId = this.defaultId;
          this.$refs.refHistory.initialize(this.selectedId);
        }
      }
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
.select_size {
  max-width: 90%;
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
