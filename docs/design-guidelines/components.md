```

**Select Dropdown:**
```jsx
<select
  className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600
             rounded-md bg-white dark:bg-gray-800
             text-gray-900 dark:text-gray-100
             focus:outline-none focus:ring-2 focus:ring-blue-500">
  <option>Option 1</option>
  <option>Option 2</option>
</select>
```

**Checkbox:**
```jsx
<label className="flex items-center gap-2 cursor-pointer">
  <input
    type="checkbox"
    className="w-4 h-4 text-blue-600 border-gray-300 rounded
               focus:ring-2 focus:ring-blue-500"
  />
  <span className="text-sm text-gray-700 dark:text-gray-300">
    Remember me
  </span>
</label>
```

### Modals

**Modal Overlay:**
```jsx
<div className="fixed inset-0 bg-black bg-opacity-50 flex items-center
                justify-center z-50 p-4">
  <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl
                  max-w-md w-full p-6">
    {/* Modal content */}
  </div>
</div>
```

**Modal Header:**
```jsx
<div className="flex items-center justify-between mb-4">
  <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
    Create Project
  </h2>
  <button
    className="text-gray-500 hover:text-gray-700 dark:hover:text-gray-300"
    onClick={onClose}
  >
    <X size={20} />
  </button>
</div>
```

### Navigation

**Sidebar Item (Active):**
```jsx
<button className="flex items-center gap-3 w-full px-4 py-3
                   bg-blue-50 dark:bg-blue-900/20 text-blue-600
                   dark:text-blue-400 font-medium rounded-lg">
  <Home size={20} />
  <span>Dashboard</span>
</button>
```

**Sidebar Item (Inactive):**
```jsx
<button className="flex items-center gap-3 w-full px-4 py-3
                   text-gray-700 dark:text-gray-300
                   hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
  <Folder size={20} />
  <span>Projects</span>
</button>
```

---

## Icons

### Icon Library

**Lucide React** is used throughout the application for consistent, customizable icons.

**Installation:**
```bash
npm install lucide-react
```

**Usage:**
```jsx
import { Home, Folder, Users, CheckSquare, Plus, X } from 'lucide-react';

<Home size={20} />
<Folder size={24} className="text-blue-600" />
```

### Icon Sizes

**Standard Sizes:**
- **16px:** Small icons in badges, tight spaces
- **20px:** Default icon size (buttons, navigation)
- **24px:** Larger icons (headings, emphasis)
- **32px+:** Hero icons, empty states

**Size Classes:**
```jsx
<Icon size={16} /> {/* Small */}
<Icon size={20} /> {/* Default */}
<Icon size={24} /> {/* Large */}
```

### Common Icons

**Navigation:**
- Home, Folder, Users, Settings, Bell

**Actions:**
- Plus, Edit, Trash2, X, Check, ChevronDown, ChevronRight

**Status:**
- CheckCircle, XCircle, AlertCircle, Info

**Other:**
- Calendar, Clock, Filter, Search, MoreVertical

### Icon Color Guidelines

**Navigation:** Inherit text color (text-gray-700, etc.)
**Actions:** gray-600 (light), gray-400 (dark)
**Status:**
- Success: text-green-600
- Error: text-red-600
- Warning: text-yellow-600
- Info: text-blue-600

---

## Dark Mode

### Implementation

**Toggle Mechanism:**
- Redux slice manages theme state
- Theme persisted to localStorage
- Document root class toggled (`dark` class)

**Usage:**
```jsx
// Tailwind dark mode variant
<div className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
  Content
</div>
```

### Dark Mode Color Adjustments

**Backgrounds:**
- Light: white, gray-50, gray-100
- Dark: gray-900, gray-800, gray-700

**Text:**
- Light: gray-900, gray-700, gray-600
- Dark: gray-100, gray-300, gray-400

**Borders:**
- Light: gray-200, gray-300
- Dark: gray-700, gray-600

**Shadows:**
- Light: Standard shadows (shadow-md, shadow-lg)
- Dark: Lighter/no shadows (or dark border instead)

### Dark Mode Best Practices

1. **Always define dark mode variant:** `bg-white dark:bg-gray-800`
2. **Test both modes:** Ensure readability in both themes
3. **Use semantic colors:** Adapt badges, buttons to dark mode
4. **Reduce contrast:** Dark mode uses softer contrasts
5. **Adjust shadows:** Lighter or replace with borders in dark mode

---

## Responsive Design

### Breakpoints

**Tailwind Breakpoints:**
```css
sm:   640px   /* Small tablets, large phones */
md:   768px   /* Tablets */
lg:   1024px  /* Desktops, laptops */
xl:   1280px  /* Large desktops */
2xl:  1536px  /* Extra large screens */
```

### Mobile-First Approach

**Base styles apply to mobile, then override for larger screens:**
```jsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  {/* Mobile: 1 column, Tablet: 2 columns, Desktop: 3 columns */}
</div>
```

### Responsive Patterns

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

**Sidebar:**
```jsx
<aside className="hidden lg:block w-64">
  {/* Desktop sidebar, hidden on mobile */}
</aside>
```

### Touch Targets

**Mobile Touch Guidelines:**
- Minimum touch target: 44x44px (iOS) / 48x48px (Android)
- Button padding: py-3 (12px vertical) for mobile
- Adequate spacing between clickable elements

