16:   4rem     (64px)
20:   5rem     (80px)
24:   6rem     (96px)
```

### Spacing Guidelines

**Component Padding:**
- **Cards:** p-4 or p-6
- **Buttons:** px-4 py-2 (small), px-6 py-3 (large)
- **Modals:** p-6 or p-8
- **Page Container:** px-4 md:px-6 lg:px-8

**Component Margins:**
- **Section Spacing:** mb-6 or mb-8
- **Element Spacing:** mb-4 (between elements)
- **Tight Spacing:** mb-2 (related items)

**Grid Gaps:**
- **Card Grids:** gap-4 or gap-6
- **Form Fields:** gap-4
- **Tight Layouts:** gap-2

### Spacing Examples

**Card:**
```jsx
<div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
  {/* Content */}
</div>
```

**Section:**
```jsx
<section className="mb-8">
  <h2 className="text-xl font-semibold mb-4">Section Title</h2>
  {/* Content */}
</section>
```

**Grid:**
```jsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
  {/* Cards */}
</div>
```

---

## Components

### Buttons

**Primary Button:**
```jsx
<button className="px-4 py-2 bg-blue-600 text-white font-medium rounded-md
                   hover:bg-blue-700 transition-colors duration-200">
  Create Project
</button>
```

**Secondary Button:**
```jsx
<button className="px-4 py-2 bg-gray-200 dark:bg-gray-700
                   text-gray-900 dark:text-gray-100 font-medium rounded-md
                   hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors">
  Cancel
</button>
```

**Danger Button:**
```jsx
<button className="px-4 py-2 bg-red-600 text-white font-medium rounded-md
                   hover:bg-red-700 transition-colors">
  Delete
</button>
```

**Icon Button:**
```jsx
<button className="p-2 text-gray-600 dark:text-gray-400
                   hover:text-gray-900 dark:hover:text-gray-100
                   hover:bg-gray-100 dark:hover:bg-gray-700 rounded-md">
  <Icon size={20} />
</button>
```

**Button Sizes:**
- **Small:** px-3 py-1.5 text-sm
- **Medium (default):** px-4 py-2 text-base
- **Large:** px-6 py-3 text-lg

### Cards

**Standard Card:**
```jsx
<div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6
                hover:shadow-lg transition-shadow">
  <h3 className="text-lg font-semibold mb-2">Card Title</h3>
  <p className="text-gray-600 dark:text-gray-400">Card content...</p>
</div>
```

**Clickable Card:**
```jsx
<div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6
                cursor-pointer hover:shadow-lg transition-shadow
                hover:border-blue-500 border border-transparent">
  {/* Content */}
</div>
```

### Badges

**Status Badge (TODO):**
```jsx
<span className="px-2 py-1 text-xs font-medium rounded-full
                 bg-gray-100 text-gray-800
                 dark:bg-gray-700 dark:text-gray-300">
  Todo
</span>
```

**Status Badge (In Progress):**
```jsx
<span className="px-2 py-1 text-xs font-medium rounded-full
                 bg-blue-100 text-blue-800
                 dark:bg-blue-900 dark:text-blue-200">
  In Progress
</span>
```

**Status Badge (Done):**
```jsx
<span className="px-2 py-1 text-xs font-medium rounded-full
                 bg-green-100 text-green-800
                 dark:bg-green-900 dark:text-green-200">
  Done
</span>
```

**Priority Badge (High):**
```jsx
<span className="px-2 py-1 text-xs font-medium rounded-full
                 bg-red-100 text-red-800
                 dark:bg-red-900 dark:text-red-200">
  High
</span>
```

**Priority Badge (Medium):**
```jsx
<span className="px-2 py-1 text-xs font-medium rounded-full
                 bg-yellow-100 text-yellow-800
                 dark:bg-yellow-900 dark:text-yellow-200">
  Medium
</span>
```

**Priority Badge (Low):**
```jsx
<span className="px-2 py-1 text-xs font-medium rounded-full
                 bg-blue-100 text-blue-800
                 dark:bg-blue-900 dark:text-blue-200">
  Low
</span>
```

### Forms

**Input Field:**
```jsx
<div className="mb-4">
  <label className="block text-sm font-medium mb-2
                    text-gray-700 dark:text-gray-300">
    Project Name
  </label>
  <input
    type="text"
    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600
               rounded-md bg-white dark:bg-gray-800
               text-gray-900 dark:text-gray-100
               focus:outline-none focus:ring-2 focus:ring-blue-500
               focus:border-transparent"
    placeholder="Enter project name"
  />
</div>
```

**Textarea:**
```jsx
<textarea
  className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600
             rounded-md bg-white dark:bg-gray-800
             text-gray-900 dark:text-gray-100
             focus:outline-none focus:ring-2 focus:ring-blue-500
             resize-none"
  rows="4"
  placeholder="Enter description"
/>
