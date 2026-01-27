// Menu configuration for each user role
// This defines what menu items appear for PASSENGER, DRIVER, and ADMIN roles

export type UserRole = 'PASSENGER' | 'DRIVER' | 'ADMIN';

export interface SidebarMenuItem {
  label: string;
  icon: string;
  path?: string;
  action?: string; // For special actions like buttons
  class?: string;  // For special styling
}

export const SIDEBAR_MENU_CONFIG: Record<UserRole, SidebarMenuItem[]> = {
  // PASSENGER role - customer side functionalities
  PASSENGER: [
    {
      label: 'Dashboard',
      icon: '⊞', // grid/dashboard icon
      path: '/passenger/dashboard'
    },
    {
      label: 'Current Ride',
      icon: '◆', // diamond/location icon
      path: '/passenger/current-ride'
    },
    {
      label: 'Scheduled Rides',
      icon: '📅',
      path: '/passenger/scheduled-rides'
    },
    {
      label: 'Ride History',
      icon: '📜', // history/time icon
      path: '/passenger/ride-history'
    },
    {
      label: 'Favourite Rides',
      icon: '❤️', // heart icon
      path: '/passenger/favourite-rides'
    },
    {
      label: 'Live Chat',
      icon: '💬', // chat icon
      path: '/passenger/live-chat'
    },
    {
      label: 'Settings',
      icon: '⚙', // settings/gear icon
      path: '/passenger/settings'
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
      label: 'Current Ride',
      icon: '◆', // diamond/location icon
      path: '/driver/current-ride'
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
      label: 'Live Chat',
      icon: '💬',
      path: '/admin/live-chat'
    },{
      label:'Driver Changes',
      icon:'🛠',
      path: '/admin/driver-changes'
    },
    {
      label:'Register new driver',
      icon:'➕',
      path: '/admin/register-new-driver'
    }
  ]
};
