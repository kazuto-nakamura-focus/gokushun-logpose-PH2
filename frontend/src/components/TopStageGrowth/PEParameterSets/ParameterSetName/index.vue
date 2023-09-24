<template>
  <v-container>
    <div>
      <div class="text-subtitle-1">パラメータセット名</div>
      <v-select
        v-model="selectedId"
        :items="parameterList"
        item-text="parameterName"
        item-value="id"
        class="select_size ml-1"
        @change="getItems"
        v-if="isEditMode"
      ></v-select>
      <v-subheader class="ma-0 pa-0" v-if="!isEditMode">{{
        photosynthesisData.photosynthesisTitle
      }}</v-subheader>
      <div>
        <div class="text-subtitle-1" v-if="isEditMode">編集前</div>

        <v-container class="photosynthesis">
          <v-row>
            <v-col cols="5">
              <v-row>
                <div class="text-subtitle-1">イールド値モデルパラメータ</div>
              </v-row>
            </v-col>
            <v-col cols="1"></v-col>
            <v-col cols="6">
              <v-row>
                <div class="text-subtitle-1">ワイプル分布モデルパラメータ</div>
              </v-row>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="5">
              <v-row>
                <v-subheader class="ma-0 pa-1">f</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="photosynthesisData.photosynthesisF"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">g</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="photosynthesisData.photosynthesisG"
                ></v-text-field>
              </v-row>
            </v-col>
            <v-col cols="1"></v-col>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-1">α</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="photosynthesisData.photosynthesisA"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">β</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="photosynthesisData.photosynthesisB"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">λ</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  readonly
                  :value="photosynthesisData.photosynthesisL"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>
      <div v-if="!isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="photosynthesisData.content"
          single-line
          full-width
          filled
          readonly
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          :value="photosynthesisData.author"
          single-line
          full-width
          filled
          readonly
        ></v-text-field>
      </div>

      <div v-if="isEditMode">
        <div class="text-subtitle-1">編集後</div>
        <v-container class="photosynthesis">
          <v-row>
            <v-col cols="5">
              <v-row>
                <div class="text-subtitle-1">イールド値モデルパラメータ</div>
              </v-row>
            </v-col>
            <v-col cols="1"></v-col>
            <v-col cols="6">
              <v-row>
                <div class="text-subtitle-1">ワイプル分布モデルパラメータ</div>
              </v-row>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="5">
              <v-row>
                <v-subheader class="ma-0 pa-1">f</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.photosynthesisF"
                  @change="onDisable"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">g</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.photosynthesisG"
                  @change="onDisable"
                ></v-text-field>
              </v-row>
            </v-col>
            <v-col cols="1"></v-col>
            <v-col cols="6">
              <v-row>
                <v-subheader class="ma-0 pa-1">α</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.photosynthesisA"
                  @change="onDisable"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">β</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.photosynthesisB"
                  @change="onDisable"
                ></v-text-field>
                <v-subheader class="ma-0 pa-1">λ</v-subheader>
                <v-text-field
                  class="ma-0 pa-1"
                  dense
                  hide-details="auto"
                  outlined
                  v-model.trim.number="afterParameterSetData.photosynthesisL"
                  @change="onDisable"
                ></v-text-field>
              </v-row>
            </v-col>
          </v-row>
        </v-container>
      </div>
      <div v-if="isEditMode">
        <div class="text-subtitle-1">説明</div>
        <v-text-field
          class="ma-0 pa-0"
          :rules="[ruleContents]"
          v-model.trim.number="afterParameterSetData.content"
          single-line
          full-width
          :filled="!isEditMode"
          :readonly="!isEditMode"
        ></v-text-field>
        <div class="text-subtitle-1">編集者</div>
        <v-text-field
          class="ma-0 pa-0"
          :rules="[ruleAuthor]"
          v-model.trim.number="afterParameterSetData.author"
          single-line
          full-width
          :filled="!isEditMode"
          :readonly="!isEditMode"
        ></v-text-field>
      </div>
    </div>
    <v-card-actions>
      <v-spacer></v-spacer>
      <div v-if="!isEditMode" class="GS_ButtonArea">
        <v-btn
          color="gray"
          class="ma-2 black--text"
          elevation="2"
          @click="close"
          >キャンセル</v-btn
        >
      </div>
      <div v-if="isEditMode" class="GS_ButtonArea">
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="overwriteSave"
          :disabled="isDisabledOverrideBtn"
          >上書き保存</v-btn
        >
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="aliasSave"
          >別名保存</v-btn
        >
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          v-show="parameterList.length > 2"
          @click="deleteParameterSet"
          :disabled="isDisabledDeleteBtn"
          >削除</v-btn
        >
      </div>
    </v-card-actions>
    <v-card-actions>
      <div v-if="isEditMode" class="GS_ButtonArea">
        <v-btn
          color="gray"
          class="ma-2 black--text"
          elevation="2"
          @click="close"
          >キャンセル</v-btn
        >
      </div>
    </v-card-actions>

    <ParameterSetNameDialog
      :handleSubmit="handleSubmitParameterSetName"
      :show="isShowParameterSetName"
      confirmText="保存"
      cancelText="キャンセル"
      ref="refParameterSetName"
    />
  </v-container>
</template>

<script>
import ParameterSetNameDialog from "@/components/parts/ParameterSetName";

export default {
  name: "ParameterSetName",
  props: {
    shared: Object,
    photosynthesisData: Object,
    isEditMode: Boolean,
    parameterSetList: Array,
  },
  created() {
    // console.log("created")
    // console.log("photosynthesisTitle ", this.photosynthesisData.photosynthesisTitle)
    // console.log("created", this.afterParameterSetData)
    this.isDefaltSetName(this.photosynthesisData.photosynthesisTitle);
  },
  updated() {},
  mounted() {
    this.shared.mount(this);
  },
  data() {
    return {
      // isEditMode: this.isEditMode,
      // parameterSetLabel: "パラメータセット名",
      parameterList: this.parameterSetList,
      beforeParameterSetData: this.photosynthesisData,
      afterParameterSetData: {
        photosynthesisTitle: this.photosynthesisData.photosynthesisTitle,
        photosynthesisF: 0,
        photosynthesisG: 0,
        photosynthesisA: 0,
        photosynthesisB: 0,
        photosynthesisL: 0,
        content: this.photosynthesisData.content,
        author: this.photosynthesisData.author,
      },
      // ruleParameterSetData: (value) => !!value || "パラメータデータを入力してください。",
      ruleContents: (value) => {
        this.isDisabledOverrideBtn = !value;
        return !!value || "説明を入力してください。";
      },
      ruleAuthor: (value) => {
        this.isDisabledOverrideBtn = !value;
        return !!value || "編集者を入力してください。";
      },

      isShowParameterSetName: false,
      isDisabledDeleteBtn: false,
      isDisabledOverrideBtn: false,
      selectedId: this.photosynthesisData.id,
    };
  },
  methods: {
    initialize(list) {
      this.parameterList = list;
    },
    deleteParameterSet() {
      // alert("削除")
      this.$emit("deleteParameterSet", this.beforeParameterSetData);
    },
    close() {
      // alert("close")
      this.$emit("close");
    },
    overwriteSave() {
      // alert("overwriteSave")
      this.$emit("overwriteSave", this.afterParameterSetData);
    },
    aliasSave() {
      // alert("aliasSave")
      this.$refs.refParameterSetName.display();
    },
    onDisable() {
      this.isDisabledDeleteBtn = true;
    },
    getItems() {
      // console.log("getItems")
      // console.log("selectedId: ", this.selectedId)
      let selectSetName = "";
      for (let parameter of this.parameterList) {
        if (this.selectedId == parameter.id) {
          selectSetName = parameter.name;
        }
      }
      // console.log("selectSetName: ", selectSetName)
      if (selectSetName.indexOf("デフォルト") >= 0) {
        this.isDisabledDeleteBtn = true;
        this.isDisabledOverrideBtn = true;
      } else {
        this.isDisabledDeleteBtn = false;
        this.isDisabledOverrideBtn = false;
      }
      this.$emit("getItems", this.selectedId);
    },
    //パラメータセット名の外部から制御
    handleSubmitParameterSetName: function (name) {
      //APIリクエスト処理を追記
      // console.log(name);
      this.afterParameterSetData.photosynthesisTitle = name;
      this.$emit("aliasSave", this.afterParameterSetData);
      this.$refs.refParameterSetName.close();
    },
  },
  watch: {
    // 'afterParameterSetData.photosynthesisBeforeD': function (changeAfter) {
    //     //isNaN(changeAfter)
    //     if (this.check(changeAfter)) {
    //         console.log("0000")
    //     } else {
    //         console.log("111")
    //     }
    // }
  },
  components: {
    ParameterSetNameDialog,
  },
};
</script>

<style>
.photosynthesis .v-input__control {
  width: 50px;
  padding: 0%;
}

.photosynthesis .v-input__slot {
  padding: 10px;
}
</style>