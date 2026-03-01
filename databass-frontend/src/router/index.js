import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
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
    path: '/', 
    component: () => import('../views/Dashboard.vue'), 
    meta: { requiresAuth: true }, 
    redirect: '/studio', 
    children: [
      {
        path: 'studio',
        name: 'Studio',
        component: () => import('../views/Studio.vue')
      },
      {
        path: 'profile/:username', 
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      },
      {
        path: 'faq', 
        name: 'FAQ',
        component: () => import('../views/FAQ.vue')
      },
      { path: 'library', name: 'Library', component: () => import('../views/Studio.vue') },
      { path: 'discover', name: 'Discover', component: () => import('../views/Studio.vue') },
      { path: 'settings', name: 'Settings', component: () => import('../views/Studio.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuth = authStore.isAuthenticated()

  if (to.matched.some(record => record.meta.requiresAuth) && !isAuth) {
    next('/login')
  } 
  else if ((to.name === 'Login' || to.name === 'Register') && isAuth) {
    next('/studio') 
  } 
  else {
    next()
  }
})

export default router