// Menu configuration for each user role
// This defines what menu items appear for USER, DRIVER, and ADMIN roles

export type UserRole = 'USER' | 'DRIVER' | 'ADMIN';

export interface SidebarMenuItem {
  label: string;
  icon: string;
  path?: string;
  action?: string; // For special actions like PANIC button
  class?: string;  // For special styling
}

export const SIDEBAR_MENU_CONFIG: Record<UserRole, SidebarMenuItem[]> = {
  // USER role - customer side functionalities
  USER: [
    {
      label: 'Dashboard',
      icon: '⊞', // grid/dashboard icon
      path: '/user/dashboard'
    },
    {
      label: 'Current Ride',
      icon: '◆', // diamond/location icon
      path: '/user/current-ride'
    },
    {
      label: 'Ride History',
      icon: '⏱', // history/time icon
      path: '/user/ride-history'
    },
    {
      label: 'Favourite Rides',
      icon: '★', // star icon
      path: '/user/favourite-rides'
    },
    {
      label: 'Live Chat',
      icon: '💬', // chat icon
      path: '/user/live-chat'
    },
    {
      label: 'PANIC',
      icon: '⚠', // warning/panic icon
      action: 'panic',
      class: 'panic-btn'
    },
    {
      label: 'Settings',
      icon: '⚙', // settings/gear icon
      path: '/user/settings'
    }
  ],

  // DRIVER role - driver side functionalities
  DRIVER: [
    {
      label: 'Dashboard',
      icon: '⊞',
      path: '/driver/dashboard'
    },
    {
      label: 'Ride History',
      icon: '⏱',
      path: '/driver/ride-history'
    },
    {
      label: 'Live Chat',
      icon: '💬',
      path: '/driver/live-chat'
    },
    {
      label: 'PANIC',
      icon: '⚠',
      action: 'panic',
      class: 'panic-btn'
    },
    {
      label: 'Settings',
      icon: '⚙',
      path: '/driver/settings'
    }
  ],

  // ADMIN role - administrator side functionalities
  ADMIN: [
    {
      label: 'Register Driver',
      icon: '👤',
      path: '/admin/register-driver'
    },
    {
      label: 'Ride History',
      icon: '⏱',
      path: '/admin/ride-history'
    },
    {
      label: 'Block Users',
      icon: '🚫',
      path: '/admin/block-users'
    },
    {
      label: 'PANIC Status',
      icon: '⚠',
      path: '/admin/panic-status'
    },
    {
      label: 'Live Chat',
      icon: '💬',
      path: '/admin/live-chat'
    }
  ]
};
