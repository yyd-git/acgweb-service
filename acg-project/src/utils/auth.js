export function getUserIdFromToken() {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    const payload = token.split(".")[1];
    const decoded = JSON.parse(
      decodeURIComponent(
        atob(payload)
          .split("")
          .map(c => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join("")
      )
    );
    return decoded.userId || null;
  } catch (e) {
    console.error("解析 token 失败", e);
    return null;
  }
}
