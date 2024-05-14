<!--着果量着果負担表示画面-->
<template>
  <v-container dense>
    <v-card elevation="0" class="ma-1">
      <v-card-title sytle="font-size:11pt">実測値入力</v-card-title>
      <ph-2-fruits-bearing-input
        v-if="this.menu != null"
        ref="ph2FruitsBearingInput"
        @mounted="ph2FruitsBearingInputMounted"
      ></ph-2-fruits-bearing-input>
      <ph-2-fruits-bearingdetail>
        v-if="this.menu != null"
        ref="Ph2FruitsBearingdetail"
        @mounted="Ph2FruitsBearingdetailMounted"
        >
      </ph-2-fruits-bearingdetail>
    </v-card>
  </v-container>
</template>

<script>
import Ph2FruitsBearingInput from "@/components-v1/FruitBearing/着果量着果負担入力部分.vue";
import Ph2FruitsBearingdetail from "@/components-v1/FruitBearing/着果量詳細表示部分.vue";

export default {
  data() {
    return {
      menu: null,
    };
  },
  components: {
    Ph2FruitsBearingInput,
    Ph2FruitsBearingdetail,
  },
  mounted() {},
  methods: {
    initialize: function (menu) {
      this.menu = menu;
      this.$nextTick(function () {
        if (this.$refs.ph2FruitsBearingInput !== undefined) {
          this.$refs.ph2FruitsBearingInput.initialize(this.menu);
        }
      });
    },
    ph2FruitsBearingInputMounted() {
      if (this.menu != null) {
        this.$nextTick(function () {
          this.$refs.ph2FruitsBearingInput.initialize(this.menu);
        });
      }
    },
    Ph2FruitsBearingdetailMounted() {},
  },
};
</script>

<style lang="scss" scoped>
@import "@/style/common.css";
.datePicker {
  position: fixed;
  display: none;
  z-index: 200;
  border: 1px ridge #ff66ff;
}

.divider_top {
  margin-bottom: 10px;
  color: "black";
}

.divider_center {
  margin-top: 10px;
  margin-bottom: 10px;
  color: "black";
}

.divider_bottom {
  margin-top: 10px;
  color: "black";
}

.text_field_size {
  min-width: 60px;
}
</style>
