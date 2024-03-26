import Axios from "axios";
import Cookies from "js-cookie";
import { axiosEnvConfig } from "@/config/envConfig";

const axios = Axios.create({
  baseURL: axiosEnvConfig.baseUrl,
  withCredentials: true,
});
axios.interceptors.request.use(config => {
  config.headers = {
    "Content-Type": "application/json",
    //   "X-Requested-With": "XMLHttpRequest",
    //   "Access-Control-Allow-Origin": "*", // 注意
    'Authorization': Cookies.get('id') + " " + Cookies.get('at')
  }
  return config
})

axios.defaults.timeout = 60 * 1000;
axios.interceptors.response.use(
  (response) => response, // 成功時のresponseはそのまま返す
  (error) => {
    console.log(error);
    window.location.href =
      "https://id.heroku.com/oauth/authorize?client_id=2faedc8a-eeb0-4956-a93d-0c7c82181bf8&response_type=code&scope=identity&state=shufvel9872";

  }
);
export default axios;
