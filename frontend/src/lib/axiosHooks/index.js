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
    "Authorization": "bearer 1000",
    //   "X-Requested-With": "XMLHttpRequest",
    //   "Access-Control-Allow-Origin": "*", // 注意
    'Heroku': '"' + Cookies.get('id') + " " + Cookies.get('at') + '"',
  }
  return config
})
var test = 0;
axios.defaults.timeout = 60 * 1000;
axios.interceptors.response.use(
  (response) => response, // 成功時のresponseはそのまま返す
  (error) => {
    console.log(error);
    test++;
    if (test == 1)
      window.location.href =
        "https://id.heroku.com/oauth/authorize?client_id=2faedc8a-eeb0-4956-a93d-0c7c82181bf8&response_type=code&scope=identity&state=shufvel9872";

  }
);
export default axios;
