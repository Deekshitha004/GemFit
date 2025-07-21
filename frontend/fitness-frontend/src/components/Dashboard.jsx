import React, { useEffect, useState } from 'react';
import ActivityForm from './ActivityForm';
import ActivityList from './ActivityList';
import { getActivities, deleteActivity } from '../services/api'; // ← Add deleteActivity

const Dashboard = () => {
  const [activities, setActivities] = useState([]);

  const fetchActivities = async () => {
    try {
      const res = await getActivities();
      setActivities(res.data);
    } catch (err) {
      console.error('Failed to fetch activities:', err);
    }
  };

  const handleAddActivity = (newActivity) => {
    console.log('Adding to state:', newActivity);
    setActivities(prev => [newActivity, ...prev]);
  };

  const handleDeleteActivity = async (id) => {
    try {
      await deleteActivity(id); // ← Backend delete call
      setActivities(prev => prev.filter(a => a.id !== id)); // Update UI after success
    } catch (err) {
      console.error('Delete failed:', err);
    }
  };

  useEffect(() => {
    fetchActivities();
  }, []);

  return (
    <>
      <ActivityForm onActivityAdded={handleAddActivity} />
      <ActivityList activities={activities} onDelete={handleDeleteActivity} />
    </>
  );
};

export default Dashboard;
