import { defineConfig, presetUno, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetIcons({
      scale: 1.2,
      extraProperties: {
        'display': 'inline-block',
        'vertical-align': 'middle'
      }
    })
  ],
  shortcuts: {
    'flex-center': 'flex items-center justify-center',
    'flex-between': 'flex items-center justify-between',
    'card-base': 'bg-white rounded-lg shadow-sm',
    'text-primary': 'text-[#18a058]',
    'bg-primary': 'bg-[#18a058]',
    'border-primary': 'border-[#18a058]'
  },
  theme: {
    colors: {
      primary: {
        DEFAULT: '#18a058',
        light: '#36ad6a',
        dark: '#0c7a43'
      }
    }
  },
  safelist: [
    'i-carbon-dashboard',
    'i-carbon-settings',
    'i-carbon-user-multiple',
    'i-carbon-user-role',
    'i-carbon-locked',
    'i-carbon-menu',
    'i-carbon-cube',
    'i-carbon-help',
    'i-carbon-chevron-down',
    'i-carbon-user',
    'i-carbon-logout',
    'i-carbon-add',
    'i-carbon-search',
    'i-carbon-reset',
    'i-carbon-edit',
    'i-carbon-trash-can',
    'i-carbon-checkmark',
    'i-carbon-checkmark-filled',
    'i-carbon-close',
    'i-carbon-document',
    'i-carbon-activity',
    'i-carbon-user-avatar',
    'i-carbon-operations',
    'i-carbon-data-vis-4',
    'i-carbon-arrow-up',
    'i-carbon-arrow-down',
    'i-carbon-code',
    'i-carbon-data-base',
    'i-carbon-time',
    'i-carbon-login',
    'i-carbon-home',
    'i-carbon-chat-bot',
    'i-carbon-chat-launch',
    'i-carbon-building'
  ]
})