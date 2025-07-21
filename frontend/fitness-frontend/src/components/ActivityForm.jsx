import {
  Box, Button, FormControl, InputLabel, MenuItem,
  Select, TextField
} from '@mui/material';
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { addActivity } from '../services/api';

const BLANK = { type: 'RUNNING', duration: '', caloriesBurned: '', additionalMetrics: {} };

const ActivityForm = ({ onActivityAdded = () => {} }) => {
  const [activity, setActivity] = useState(BLANK);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      console.log('Submitting activity:', activity); // Debug log
      
      const response = await addActivity(activity);
      console.log('API response:', response); // Debug log
      
      // Handle different response structures
      const savedActivity = response?.data || response;
      console.log('Saved activity:', savedActivity); // Debug log
      
      if (savedActivity) {
        onActivityAdded(savedActivity);
        setActivity(BLANK);
        console.log('Activity added successfully'); // Debug log
      } else {
        console.error('No activity returned from API');
      }
      
    } catch (err) {
      console.error('Add activity failed:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mb: 4 }}>
      <FormControl fullWidth sx={{ mb: 2 }}>
        <InputLabel>Activity Type</InputLabel>
        <Select
          value={activity.type}
          onChange={(e) => setActivity({ ...activity, type: e.target.value })}
          disabled={loading}
        >
          {['RUNNING', 'WALKING', 'CYCLING', 'YOGA','WEIGHT TRAINING','JOGGING','CARDIO','STRETCHING','BOXING','SWIMMING','GYM'].map(t => (
            <MenuItem key={t} value={t}>{t.charAt(0) + t.slice(1).toLowerCase()}</MenuItem>
          ))}
        </Select>
      </FormControl>

      <TextField
        fullWidth 
        label="Duration (Minutes)" 
        type="number" 
        sx={{ mb: 2 }}
        value={activity.duration}
        onChange={(e) => setActivity({ ...activity, duration: e.target.value })}
        disabled={loading}
        required
      />

      <TextField
        fullWidth 
        label="Calories Burned" 
        type="number" 
        sx={{ mb: 2 }}
        value={activity.caloriesBurned}
        onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })}
        disabled={loading}
        required
      />

      <Button 
        type="submit" 
        variant="contained" 
        disabled={loading || !activity.duration || !activity.caloriesBurned}
      >
        {loading ? 'Adding...' : 'Add Activity'}
      </Button>
    </Box>
  );
};

ActivityForm.propTypes = { onActivityAdded: PropTypes.func };

export default ActivityForm;