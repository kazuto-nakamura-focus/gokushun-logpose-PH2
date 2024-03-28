import Axios from "axios";
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
    // 'Heroku': '"' + Cookies.get('id') + " " + Cookies.get('at') + '"',
  }
  return config
})
axios.defaults.timeout = 60 * 1000;
axios.interceptors.response.use(
  (response) => {
    const { status, message } = response["data"];
    if (status == -1) {
      window.location.href = message;
    }
    return response;
  },
  (error) => {
    console.log(error);
    return error;
  }
);
export default axios;
