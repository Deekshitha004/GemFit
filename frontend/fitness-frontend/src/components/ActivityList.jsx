import { Card, CardContent, Typography, Button } from '@mui/material'; // ← add Button here
import Grid2 from "@mui/material/Grid";
import { useNavigate } from 'react-router';

const ActivityList = ({ activities = [], onDelete = () => {} }) => {
  const navigate = useNavigate();

  return (
    <Grid2 container spacing={3} justifyContent="flex-start">
  {activities.map((activity) => (
    <Grid2 item xs={12} sm={6} md={4} key={activity.id}>
      <Card sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        borderRadius: 3,
        boxShadow: 3,
        transition: '0.3s',
        '&:hover': { boxShadow: 6 },
        p: 2
      }}>
        <CardContent onClick={() => navigate(`/activities/${activity.id}`)} sx={{ cursor: 'pointer' }}>
          <Typography variant="h6" sx={{ textTransform: 'uppercase', fontWeight: 600 }}>
            {activity.type}
          </Typography>
          <Typography>Duration: {activity.duration} mins</Typography>
          <Typography>Calories: {activity.caloriesBurned}</Typography>
        </CardContent>
        <Button
          variant="outlined"
          color="error"
          onClick={() => onDelete(activity.id)}
          sx={{ alignSelf: 'flex-end', mt: 1 }}
        >
          Delete
        </Button>
      </Card>
    </Grid2>
  ))}
</Grid2>
  );
};

export default ActivityList;
