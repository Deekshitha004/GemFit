// src/components/ActivityDetail.jsx
import { useEffect, useState } from 'react';
import { useParams }        from 'react-router';
import { getActivityDetail } from '../services/api';
import {
  Box, Card, CardContent, Divider, Typography, CircularProgress,
} from '@mui/material';

// small helper to deduplicate bullet-lists
const dedupe = (arr = []) => Array.from(new Set(arr.map(t => t.trim())));

const ActivityDetail = () => {
  const { id } = useParams();

  const [recommendation, setRecommendation] = useState(null);
  const [status,         setStatus]         = useState('loading'); // loading | pending | ready | error

  useEffect(() => {
    let timer;
    let retries = 0;
    const maxRetries = 5;

    const fetchDetail = async () => {
      try {
        const { data } = await getActivityDetail(id);   // GET /recommendations/activity/{id}
        setRecommendation(data);
        setStatus('ready');
      } catch (err) {
        if (err.response?.status === 404 && retries < maxRetries) {
          retries += 1;
          setStatus('pending');
          timer = setTimeout(fetchDetail, 2000);        // retry in 2 s
        } else {
          console.error(err);
          setStatus('error');
        }
      }
    };

    fetchDetail();
    return () => clearTimeout(timer);                  // clean-up on unmount
  }, [id]);

  /* ---------- UI states ---------- */
  if (status === 'loading') {
    return (
      <Box textAlign="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (status === 'pending') {
    return (
      <Box textAlign="center" mt={4}>
        <Typography variant="h6">
          Analysing workout – please wait…
        </Typography>
      </Box>
    );
  }

  if (status === 'error') {
    return (
      <Box textAlign="center" mt={4}>
        <Typography variant="h6" color="error">
          Unable to load recommendation.
        </Typography>
      </Box>
    );
  }

  if (!recommendation) return null; // defensive

  /* ---------- render ready state ---------- */
  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
      {/* Activity summary */}
      <Card sx={{ mb: 3, borderRadius: 3, boxShadow: 4 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom fontWeight={600}>Activity Details</Typography>
          <Typography><strong>Type:</strong> {recommendation.activityType}</Typography>
          <Typography><strong>Duration:</strong> {recommendation.duration} minutes</Typography>
          <Typography><strong>Calories Burned:</strong> {recommendation.caloriesBurned}</Typography>
          <Typography><strong>Date:</strong> {new Date(recommendation.createdAt).toLocaleString()}</Typography>
        </CardContent>
      </Card>

      {/* AI recommendation */}
      <Card sx={{ borderRadius: 3, boxShadow: 4 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom fontWeight={600}>AI Recommendation</Typography>

          <Divider sx={{ my: 2 }} />

          <Typography variant="subtitle1" fontWeight={600}>Analysis</Typography>
          <Typography paragraph sx={{ whiteSpace: 'pre-line' }}>
            {recommendation.recommendation}
          </Typography>

          {recommendation.improvements?.length > 0 && (
            <>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle1" fontWeight={600}>Improvements</Typography>
              {dedupe(recommendation.improvements).map((imp, i) => (
                <Typography key={i} paragraph>• {imp}</Typography>
              ))}
            </>
          )}

          {recommendation.suggestions?.length > 0 && (
            <>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle1" fontWeight={600}>Suggestions</Typography>
              {recommendation.suggestions.map((sug, i) => (
                <Typography key={i} paragraph>• {sug}</Typography>
              ))}
            </>
          )}

          {recommendation.safety?.length > 0 && (
            <>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle1" fontWeight={600}>Safety Guidelines</Typography>
              {recommendation.safety.map((safe, i) => (
                <Typography key={i} paragraph>• {safe}</Typography>
              ))}
            </>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default ActivityDetail;
