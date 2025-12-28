// // import axios from "axios";

// // // 创建 Axios 实例
// // const http = axios.create({
// //   baseURL: "http://localhost:8090", // 网关地址
// //   timeout: 10000,
// // });

// // // 请求拦截器：统一加 Authorization header
// // http.interceptors.request.use(
// //   config => {
// //     const token = localStorage.getItem("token");
// //     if (token) {
// //       config.headers.Authorization = `Bearer ${token.trim()}`;
// //     }
// //     return config;
// //   },
// //   error => Promise.reject(error)
// // );

// // // ✅ 响应拦截器：返回后端 Result
// // http.interceptors.response.use(
// //   response => response.data,
// //   error => {
// //     if (error.response && error.response.status === 401) {
// //       alert("登录已过期或未登录，请重新登录");
// //       localStorage.removeItem("token");
// //       window.location.href = "/login";
// //     }
// //     return Promise.reject(error);
// //   }
// // );

// // export default http;
// import axios from "axios";

// // ====== 调试开关（开发开，生产关） ======
// const DEBUG_HTTP = true;

// // 创建 Axios 实例
// const http = axios.create({
//   baseURL: "http://localhost:8090",
//   timeout: 10000,
// });

// // ================= 请求拦截器 =================
// http.interceptors.request.use(
//   config => {
//     const token = localStorage.getItem("token");
//     if (token) {
//       config.headers.Authorization = `Bearer ${token.trim()}`;
//     }

//     if (DEBUG_HTTP) {
//       console.groupCollapsed(
//         `%c➡️ ${config.method?.toUpperCase()} ${config.url}`,
//         "color:#67C23A;font-weight:bold"
//       );
//       console.log("Request config:", config);
//       console.groupEnd();
//     }

//     return config;
//   },
//   error => Promise.reject(error)
// );

// // ================= 响应拦截器 =================
// http.interceptors.response.use(
//   response => {
//     if (DEBUG_HTTP) {
//       console.groupCollapsed(
//         `%c⬅️ ${response.config.method?.toUpperCase()} ${response.config.url}`,
//         "color:#409EFF;font-weight:bold"
//       );
//       console.log("Status:", response.status);
//       console.log("Result:", response.data);
//       console.groupEnd();
//     }

//     // ⚠️ 保持你的原逻辑
//     return response.data;
//   },
//   error => {
//     if (DEBUG_HTTP) {
//       console.groupCollapsed(
//         `%c❌ HTTP ERROR ${error.config?.url}`,
//         "color:red;font-weight:bold"
//       );
//       console.log("Error:", error);
//       console.groupEnd();
//     }

//     if (error.response && error.response.status === 401) {
//       alert("登录已过期或未登录，请重新登录");
//       localStorage.removeItem("token");
//       // 给 console 一点时间显示
//       setTimeout(() => {
//         window.location.href = "/login";
//       }, 500);
//     }

//     return Promise.reject(error);
//   }
// );

// export default http;
import axios from "axios";

// ====== 调试开关（开发开，生产关） ======
const DEBUG_HTTP = true;

// 创建 Axios 实例
const http = axios.create({
  baseURL: "http://localhost:8090", // ✅ 网关地址
  timeout: 10000,
  withCredentials: true, // 如果后端需要 cookies 或 allowCredentials
});

// ================= 请求拦截器 =================
http.interceptors.request.use(
  config => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token.trim()}`;
    }

    if (DEBUG_HTTP) {
      console.groupCollapsed(
        `%c➡️ ${config.method?.toUpperCase()} ${config.url}`,
        "color:#67C23A;font-weight:bold"
      );
      console.log("Request config:", config);
      console.groupEnd();
    }

    return config;
  },
  error => Promise.reject(error)
);

// ================= 响应拦截器 =================
http.interceptors.response.use(
  response => {
    if (DEBUG_HTTP) {
      console.groupCollapsed(
        `%c⬅️ ${response.config.method?.toUpperCase()} ${response.config.url}`,
        "color:#409EFF;font-weight:bold"
      );
      console.log("Status:", response.status);
      console.log("Result:", response.data);
      console.groupEnd();
    }

    return response.data;
  },
  error => {
    if (DEBUG_HTTP) {
      console.groupCollapsed(
        `%c❌ HTTP ERROR ${error.config?.url}`,
        "color:red;font-weight:bold"
      );
      console.log("Error:", error);
      console.groupEnd();
    }

    if (error.response && error.response.status === 401) {
      alert("登录已过期或未登录，请重新登录");
      localStorage.removeItem("token");
      setTimeout(() => {
        window.location.href = "/login";
      }, 500);
    }

    return Promise.reject(error);
  }
);

export default http;
