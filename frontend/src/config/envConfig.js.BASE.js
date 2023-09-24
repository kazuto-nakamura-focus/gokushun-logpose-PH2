const axiosEnvConfig = {
  baseUrl: process.env.VUE_APP_API_BASE_URL || "",
  //  baseUrl : "http://localhost:9000/api/",
  GSIUrl: "https://msearch.gsi.go.jp/address-search/",
};

const commonEnvConfig = {
  googleMapApiKey: process.env.VUE_APP_GOOGLE_MAP_API_KEY,
};


export { commonEnvConfig, axiosEnvConfig };
