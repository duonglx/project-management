# Tailwind CSS Styling Guide

## Tailwind CSS Conventions

### 1. Utility-First Approach

- Use Tailwind utilities directly in JSX
- Avoid custom CSS unless necessary
- Extract repeated patterns to components

### 2. Class Organization

Group by category: layout → spacing → typography → colors → effects

```jsx
<div className="flex items-center justify-between p-4 text-lg font-semibold text-gray-900 bg-white rounded-lg shadow-md hover:shadow-lg">
```

### 3. Responsive Design

Mobile-first approach (base styles for mobile)
Use breakpoint prefixes: `sm:`, `md:`, `lg:`, `xl:`, `2xl:`

```jsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
```

### 4. Dark Mode

Use `dark:` variant for dark mode styles
Toggle via `themeSlice` (adds/removes `dark` class on `<html>`)

```jsx
<div className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
```

### 5. Color Palette

- **Primary:** Blue shades (blue-500, blue-600, blue-700)
- **Secondary:** Gray shades for text and backgrounds
- **Success:** Green (green-500, green-600)
- **Warning:** Yellow (yellow-500, yellow-600)
- **Danger:** Red (red-500, red-600)
- **Info:** Blue (blue-400, blue-500)

### 6. Spacing Scale

Use Tailwind's default scale (0, 1, 2, 4, 6, 8, 12, 16, 24, 32...)
- Consistent padding: `p-4`, `p-6`, `p-8`
- Consistent margins: `mb-4`, `mt-6`, `mx-auto`

### 7. Typography

- **Font sizes:** `text-xs`, `text-sm`, `text-base`, `text-lg`, `text-xl`, `text-2xl`...
- **Font weights:** `font-normal`, `font-medium`, `font-semibold`, `font-bold`
- **Line heights:** Use default or specify (e.g., `leading-tight`, `leading-relaxed`)

## Common Patterns

### Card

```jsx
<div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
  {/* Content */}
</div>
```

### Button (Primary)

```jsx
<button className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
  Click Me
</button>
```

### Button (Secondary)

```jsx
<button className="px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-gray-100 rounded-md hover:bg-gray-300 dark:hover:bg-gray-600">
  Cancel
</button>
```

### Input

```jsx
<input className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
```

### Badge

```jsx
<span className="px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200">
  Status
</span>
```

### Modal Overlay

```jsx
<div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
  {/* Modal content */}
</div>
```

## Custom CSS (Global Styles)

**src/index.css:**
```css
@import "tailwindcss";

/* Custom CSS variables (if needed) */
:root {
  --color-primary: #3b82f6;
  --color-secondary: #6b7280;
}

/* Global base styles */
body {
  @apply bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100;
}

/* Utility classes (if needed) */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
```

## Responsive Breakpoints

### Tailwind Default Breakpoints

```css
sm:   640px   /* Small tablets, large phones */
md:   768px   /* Tablets */
lg:   1024px  /* Desktops, laptops */
xl:   1280px  /* Large desktops */
2xl:  1536px  /* Extra large screens */
```

### Usage Examples

**Container:**
```jsx
<div className="px-4 md:px-6 lg:px-8 max-w-7xl mx-auto">
  {/* Responsive padding, centered with max width */}
</div>
```

**Grid Layout:**
```jsx
<div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
  {/* Responsive columns: 1 → 2 → 3 → 4 */}
</div>
```

**Typography:**
```jsx
<h1 className="text-xl md:text-2xl lg:text-3xl font-bold">
  {/* Responsive font size */}
</h1>
```

**Hide/Show Elements:**
```jsx
<div className="hidden md:block">Desktop only</div>
<div className="md:hidden">Mobile only</div>
```

## Dark Mode Implementation

### Theme Toggle (Redux)

```javascript
// src/features/themeSlice.js
import { createSlice } from '@reduxjs/toolkit';

const themeSlice = createSlice({
  name: 'theme',
  initialState: {
    mode: localStorage.getItem('theme') || 'light',
  },
  reducers: {
    toggleTheme(state) {
      state.mode = state.mode === 'light' ? 'dark' : 'light';
      localStorage.setItem('theme', state.mode);
      document.documentElement.classList.toggle('dark', state.mode === 'dark');
    },
  },
});
```

### Dark Mode Patterns

**Backgrounds:**
```jsx
<div className="bg-white dark:bg-gray-800">
```

**Text:**
```jsx
<p className="text-gray-900 dark:text-gray-100">
```

**Borders:**
```jsx
<div className="border border-gray-200 dark:border-gray-700">
```

**Always Define Dark Mode:**
```jsx
// Good
<div className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">

// Bad (missing dark mode)
<div className="bg-white text-gray-900">
```

## Component Styling Examples

### Project Card

```jsx
<div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 cursor-pointer hover:shadow-lg transition-shadow">
  <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
    Project Name
  </h3>
  <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">
    Description
  </p>
  <div className="flex items-center gap-2">
    <span className="px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200">
      In Progress
    </span>
    <span className="px-2 py-1 text-xs font-medium rounded-full bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200">
      High Priority
    </span>
  </div>
</div>
```

### Form Input

```jsx
<div className="mb-4">
  <label className="block text-sm font-medium mb-2 text-gray-700 dark:text-gray-300">
    Project Name
  </label>
  <input
    type="text"
    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
    placeholder="Enter project name"
  />
</div>
```

### Navigation Item

```jsx
{/* Active */}
<button className="flex items-center gap-3 w-full px-4 py-3 bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 font-medium rounded-lg">
  <Home size={20} />
  <span>Dashboard</span>
</button>

{/* Inactive */}
<button className="flex items-center gap-3 w-full px-4 py-3 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
  <Folder size={20} />
  <span>Projects</span>
</button>
```

## Best Practices

### 1. Consistency

- Reuse spacing values (p-4, p-6, mb-4, mb-6)
- Use standard color palette
- Follow established component patterns

### 2. Performance

- Avoid inline styles (use Tailwind classes)
- Use CSS transitions (GPU-accelerated)
- Minimize DOM depth

### 3. Maintainability

- Extract repeated patterns to components
- Use semantic class ordering
- Comment complex layouts

### 4. Accessibility

- Ensure color contrast meets WCAG AA
- Provide focus states (focus:ring-2)
- Use semantic HTML with Tailwind

## Transitions

**Color Transitions:**
```jsx
<button className="bg-blue-600 hover:bg-blue-700 transition-colors duration-200">
```

**Shadow Transitions:**
```jsx
<div className="shadow-md hover:shadow-lg transition-shadow duration-300">
```

**Transform Transitions:**
```jsx
<div className="transform hover:scale-105 transition-transform duration-200">
```

## Resources

- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [Tailwind UI Components](https://tailwindui.com/)
- [Headless UI](https://headlessui.com/) (accessible components)
