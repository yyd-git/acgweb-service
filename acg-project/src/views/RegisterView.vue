<template>
  <div class="container">
    <img src="https://i.pinimg.com/originals/50/2f/5d/502f5d95c5f7a1b6e2e1eeb0f9e1d1c5.png" alt="anime" class="anime-image">
    <h1>注册</h1>
    <el-input v-model="username" placeholder="用户名" class="input-box"></el-input>
    <el-input v-model="password" placeholder="密码" type="password" class="input-box"></el-input>
    <el-button type="primary" @click="register" class="btn">注册</el-button>
    <el-button type="text" @click="goLogin" class="toggle-btn">去登录</el-button>
    <div class="message" :style="{ color: messageColor }">{{ message }}</div>
  </div>
</template>

<script>
export default {
  name: "RegisterView", // ✅ 多单词命名
  data() {
    return {
      username: '',
      password: '',
      message: '',
      messageColor: 'red'
    }
  },
  methods: {
    async register() {
      if (!this.username || !this.password) {
        this.message = '请输入用户名和密码';
        this.messageColor = 'red';
        return;
      }

      const API_BASE = 'http://localhost:8090/user';
      const params = new URLSearchParams({ username: this.username, password: this.password });

      try {
        const response = await fetch(`${API_BASE}/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params,
          credentials: 'include'
        });

        const text = await response.text();
        let result;
        try { result = JSON.parse(text) } catch {
          this.message = '返回非 JSON 数据: ' + text;
          this.messageColor = 'red';
          return;
        }

        if(result.code === 1){
          this.message = result.msg;
          this.messageColor = 'lightgreen';
          setTimeout(() => this.$router.push('/login'), 1000);
        } else {
          this.message = result.msg;
          this.messageColor = 'red';
        }

      } catch(err) {
        console.error(err);
        this.message = '网络错误，请重试';
        this.messageColor = 'red';
      }
    },
    goLogin() {
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

.container {
  font-family: 'Press Start 2P', cursive;
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  width: 350px;
  text-align: center;
  color: #fff;
  box-shadow: 0 0 20px rgba(255,255,255,0.2);
  margin: 50px auto;
}
h1 { margin-bottom: 30px; color: #ffeaa7; }
.input-box { width: 100%; margin-bottom: 15px; }
.btn { width: 100%; margin: 15px 0; }
.toggle-btn { margin-top: 10px; }
.message { margin-top: 10px; font-size: 12px; height: 18px; word-break: break-word; }
.anime-image { width: 100px; margin-bottom: 20px; animation: float 2s ease-in-out infinite; }
@keyframes float { 0%,100%{transform:translateY(0);}50%{transform:translateY(-10px);} }
</style>
