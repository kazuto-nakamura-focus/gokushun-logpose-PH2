import Axios from "axios";
import { axiosEnvConfig } from "@/config/envConfig";

const axios = Axios.create({
  baseURL: axiosEnvConfig.baseUrl,
  withCredentials: true,
});

axios.defaults.timeout = 60 * 1000;
axios.interceptors.response.use(
  (response) => {
    const { status, message } = response["data"];
    if (status == -1) {
      console.log(message);
      window.location.replace(message);
    }
    return response;
  },
  (error) => {
    console.log(error);
    return error;
  }
);
export default axios;
