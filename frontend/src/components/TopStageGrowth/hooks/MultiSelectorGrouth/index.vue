<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card elevation="0" class="ma-1">
          <v-card-text class="pa-1">{{ label }}</v-card-text>
          <v-card-content>
            <v-slide-group class="pa-1" multiple>
              <v-slide-item
                v-for="(item, index) in list"
                :key="index"
                v-slot:default="{ active, toggle }"
              >
                <div @click="onCardClick(item, index, active)">
                  <v-btn
                    class="ma-1"
                    color="green"
                    :input-value="active"
                    :outlined="active ? false : true"
                    depressed
                    @click="toggle"
                  >{{ "title" in item ? item.title : item.name }}</v-btn>
                </div>
              </v-slide-item>
            </v-slide-group>
          </v-card-content>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      required: true,
    },
    data: {
      type: Array,
      required: true,
    },
    handleClick: {
      type: Function,
      required: true,
    },
  },

  mounted() {
    console.log(this.data);
  },

  data() {
    return {
      label: this.title,
      list: this.data,
    };
  },

  methods: {
    onCardClick(item, index, active, toggle) {
      this.handleClick(item, index, active);
    },
  },
};
</script>
