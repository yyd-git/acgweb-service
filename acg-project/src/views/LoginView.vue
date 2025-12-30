<template>
  <div class="container">
    <img
      :src="loginImg"
      alt="anime"
      class="anime-image"
    />
    <h1>登录</h1>

    <el-input
      v-model="username"
      placeholder="用户名"
      class="input-box"
    ></el-input>

    <el-input
      v-model="password"
      placeholder="密码"
      type="password"
      class="input-box"
    ></el-input>

    <el-button type="primary" @click="login" class="btn">
      登录
    </el-button>

    <el-button type="text" @click="goRegister" class="toggle-btn">
      去注册
    </el-button>

    <div class="message" :style="{ color: messageColor }">
      {{ message }}
    </div>
  </div>
</template>

<script>
import http from "@/utils/http";
import loginImg from "@/assets/upimg.jpg"; // ✅ 本地图片

export default {
  name: "LoginView",
  data() {
    return {
      username: "",
      password: "",
      message: "",
      messageColor: "red",
      loginImg, // ✅ 绑定图片
    };
  },
  methods: {
    async login() {
      if (!this.username || !this.password) {
        this.message = "请输入用户名和密码";
        this.messageColor = "red";
        return;
      }

      const params = new URLSearchParams({
        username: this.username,
        password: this.password,
      });

      try {
        const data = await http.post("/user/login", params, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        });

        if (data.code === 1 && data.msg === "success") {
          const token = data.data;
          localStorage.setItem("token", token);

          this.message = "登录成功";
          this.messageColor = "lightgreen";

          setTimeout(() => {
            this.$router.push("/products");
          }, 800);
        } else {
          this.message = data.msg || "登录失败";
          this.messageColor = "red";
        }
      } catch (err) {
        console.error(err);
        this.message = "网络错误，请重试";
        this.messageColor = "red";
      }
    },

    goRegister() {
      this.$router.push("/register");
    },
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap");

.container {
  font-family: "Press Start 2P", cursive;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  width: 350px;
  text-align: center;
  color: #fff;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.2);
  margin: 50px auto;
}

h1 {
  margin-bottom: 30px;
  color: #ffeaa7;
}

.input-box {
  width: 100%;
  margin-bottom: 15px;
}

.btn {
  width: 100%;
  margin: 15px 0;
}

.toggle-btn {
  margin-top: 10px;
}

.message {
  margin-top: 10px;
  font-size: 12px;
  height: 18px;
  word-break: break-word;
}

.anime-image {
  width: 100px;
  height: auto;
  margin-bottom: 20px;
  object-fit: contain;
  animation: float 2s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}
</style>
