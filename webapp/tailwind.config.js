module.exports = {
  darkMode: 'class',
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html"
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          light: '#E0E5EC',
          dark: '#2D3748',
        },
        secondary: {
          light: '#FFFFFF',
          dark: '#1A202C',
        },
        accent: {
          light: '#4F46E5',
          dark: '#7C3AED',
        }
      },
      boxShadow: {
        'neumorphic-light': '8px 8px 16px #d1d9e6, -8px -8px 16px #ffffff',
        'neumorphic-dark': '8px 8px 16px #1a202c, -8px -8px 16px #2d3748',
      }
    },
  },
  plugins: [],
}