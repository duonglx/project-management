# Design Guidelines

## Design Philosophy

The Project Management Platform follows modern design principles emphasizing clarity, consistency, and accessibility. The UI is built with Tailwind CSS 4, providing a utility-first approach to styling with comprehensive dark mode support.

**Core Principles:**
1. **Clarity:** Information hierarchy is clear and scannable
2. **Consistency:** Patterns repeat across the application
3. **Accessibility:** WCAG 2.1 AA compliance target
4. **Responsiveness:** Mobile-first, adaptive layouts
5. **Performance:** Fast interactions, minimal layout shifts

---

## Color System

### Primary Color Palette

**Blue (Primary Brand Color):**
- Used for primary actions, links, active states
- Conveys trust, professionalism, productivity

```css
blue-50:   #eff6ff  /* Lightest backgrounds */
blue-100:  #dbeafe  /* Light backgrounds, hover states */
blue-200:  #bfdbfe  /* Borders, dividers */
blue-300:  #93c5fd  /* Disabled states */
blue-400:  #60a5fa  /* Secondary actions */
blue-500:  #3b82f6  /* Primary actions, links */
blue-600:  #2563eb  /* Hover states, emphasis */
blue-700:  #1d4ed8  /* Active/pressed states */
blue-800:  #1e40af  /* Dark accents */
blue-900:  #1e3a8a  /* Darkest accents */
```

**Usage:**
- **blue-600:** Primary buttons, active navigation items, links
- **blue-500:** Hover states, secondary emphasis
- **blue-100/blue-900:** Badge backgrounds (light/dark mode)

### Neutral Colors (Gray Scale)

**Light Mode:**
```css
gray-50:   #f9fafb  /* Page background */
gray-100:  #f3f4f6  /* Card backgrounds, subtle fills */
gray-200:  #e5e7eb  /* Borders, dividers */
gray-300:  #d1d5db  /* Disabled elements */
gray-400:  #9ca3af  /* Placeholder text */
gray-500:  #6b7280  /* Secondary text */
gray-600:  #4b5563  /* Body text */
gray-700:  #374151  /* Headings */
gray-800:  #1f2937  /* Emphasis text */
gray-900:  #111827  /* Primary text, darkest elements */
```

**Dark Mode:**
```css
gray-900:  #111827  /* Page background */
gray-800:  #1f2937  /* Card backgrounds */
gray-700:  #374151  /* Elevated surfaces */
gray-600:  #4b5563  /* Borders, dividers */
gray-500:  #6b7280  /* Secondary text */
gray-400:  #9ca3af  /* Body text */
gray-300:  #d1d5db  /* Headings */
gray-200:  #e5e7eb  /* Emphasis text */
gray-100:  #f3f4f6  /* Primary text */
gray-50:   #f9fafb  /* Lightest accents */
```

### Semantic Colors

**Success (Green):**
```css
green-50:   #f0fdf4
green-100:  #dcfce7
green-500:  #22c55e  /* Success states, completed tasks */
green-600:  #16a34a  /* Hover states */
green-700:  #15803d  /* Active states */
```

**Usage:** Completed tasks, success messages, positive metrics

**Warning (Yellow/Amber):**
```css
yellow-50:   #fefce8
yellow-100:  #fef9c3
yellow-500:  #eab308  /* Warning states, medium priority */
yellow-600:  #ca8a04  /* Hover states */
yellow-700:  #a16207  /* Active states */
```

**Usage:** Medium priority tasks, warning messages, pending states

**Danger (Red):**
```css
red-50:   #fef2f2
red-100:  #fee2e2
red-500:  #ef4444  /* Error states, high priority, delete actions */
red-600:  #dc2626  /* Hover states */
red-700:  #b91c1c  /* Active states */
```

**Usage:** High priority tasks, error messages, destructive actions

**Info (Blue):**
```css
blue-50:   #eff6ff
blue-100:  #dbeafe
blue-400:  #60a5fa  /* Info messages, low priority */
blue-500:  #3b82f6  /* Default info state */
```

**Usage:** Low priority tasks, info messages, notifications

### Color Usage Guidelines

**Backgrounds:**
- **Light Mode:** gray-50 (page), white (cards)
- **Dark Mode:** gray-900 (page), gray-800 (cards)

**Text:**
- **Primary:** gray-900 (light) / gray-100 (dark)
- **Secondary:** gray-600 (light) / gray-400 (dark)
- **Tertiary:** gray-500 (light/dark)

**Borders:**
- **Default:** gray-200 (light) / gray-700 (dark)
- **Hover:** gray-300 (light) / gray-600 (dark)

**Interactive Elements:**
- **Primary Button:** blue-600 background, white text
- **Secondary Button:** gray-200 (light) / gray-700 (dark) background
- **Links:** blue-600 (light) / blue-400 (dark)

---

## Typography

### Font Stack

**System Font Stack (Tailwind Default):**
```css
font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont,
             "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
```

**Benefits:**
- Native look and feel on each platform
- No font loading delay
- Excellent performance
- Optimized legibility

### Font Sizes

**Scale:**
```css
text-xs:    0.75rem  (12px)  /* Small labels, badges */
text-sm:    0.875rem (14px)  /* Secondary text, captions */
text-base:  1rem     (16px)  /* Body text (default) */
text-lg:    1.125rem (18px)  /* Large body text, subheadings */
text-xl:    1.25rem  (20px)  /* Section headings */
text-2xl:   1.5rem   (24px)  /* Page headings */
text-3xl:   1.875rem (30px)  /* Large headings */
text-4xl:   2.25rem  (36px)  /* Hero headings */
```

**Usage:**
- **Headings:** text-2xl (page titles), text-xl (section titles)
- **Body:** text-base (default), text-sm (secondary info)
- **Labels:** text-sm (form labels), text-xs (badges, meta info)

### Font Weights

```css
font-normal:    400  /* Body text */
font-medium:    500  /* Emphasis, labels */
font-semibold:  600  /* Headings, buttons */
font-bold:      700  /* Strong emphasis, large headings */
```

**Usage:**
- **Headings:** font-semibold or font-bold
- **Body Text:** font-normal
- **Buttons/Labels:** font-medium or font-semibold
- **Emphasis:** font-medium

### Line Heights

```css
leading-tight:    1.25   /* Headings */
leading-snug:     1.375  /* Tight body text */
leading-normal:   1.5    /* Default body text */
leading-relaxed:  1.625  /* Comfortable reading */
leading-loose:    2      /* Spacious layouts */
```

**Usage:**
- **Headings:** leading-tight
- **Body Text:** leading-normal (default)
- **Long-form Content:** leading-relaxed

### Text Examples

**Page Heading:**
```jsx
<h1 className="text-2xl font-semibold text-gray-900 dark:text-gray-100">
  Dashboard
</h1>
```

**Section Heading:**
```jsx
<h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
  Recent Projects
</h2>
```

**Body Text:**
```jsx
<p className="text-base text-gray-600 dark:text-gray-400">
  Manage your projects and tasks efficiently.
</p>
```

**Small Text:**
```jsx
<span className="text-sm text-gray-500 dark:text-gray-500">
  Last updated 2 hours ago
</span>
```

---

## Spacing System

### Tailwind Spacing Scale

```css
0:    0px
1:    0.25rem  (4px)
2:    0.5rem   (8px)
3:    0.75rem  (12px)
4:    1rem     (16px)
5:    1.25rem  (20px)
6:    1.5rem   (24px)
8:    2rem     (32px)
10:   2.5rem   (40px)
12:   3rem     (48px)
