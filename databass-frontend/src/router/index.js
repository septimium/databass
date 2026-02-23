import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { 
    path: '/', 
    redirect: '/dashboard' 
  },
  { 
    path: '/login', 
    name: 'Login',
    component: () => import('../views/Login.vue') 
  },
  { 
    path: '/register', 
    name: 'Register',
    component: () => import('../views/Register.vue') 
  },
  { 
    path: '/dashboard', 
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true } 
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuth = authStore.isAuthenticated()

  if (to.meta.requiresAuth && !isAuth) {
    next('/login')
  } 
  else if ((to.name === 'Login' || to.name === 'Register') && isAuth) {
    next('/dashboard')
  } 
  else {
    next()
  }
})

export default router